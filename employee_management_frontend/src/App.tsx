import { useEffect, useState, useRef, useCallback, lazy, Suspense } from "react";
import { deleteEmployee, getEmployees } from "./services/employeeService";
import type { Employee } from "./types/employee";
import EmployeeTable from "./components/EmployeeTable";
import useDebounce from "./hooks/useDebounce";
import EmployeeForm from "./components/EmployeeForm";
import "./App.css";
import Login from "./components/Login";
import { getUserRole } from "./utils/authUtils";
import { getAnalyticsData, getSalaryOverview } from "./services/analyticsService";
import type { AnalyticsData,SalaryOverview } from "./types/analytics";

    const AnalyticsCharts = lazy(
      () => import("./components/AnalyticsCharts")
    )

function EmployeeDashboard({ onLogout, isAdmin }: { onLogout: () => void; isAdmin: boolean; }) {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [loading, setLoading] = useState(true);
    const [analytics, setAnalytics] = useState<AnalyticsData>({
      totalEmployees: 0,
      averageSalary: 0,
      departmentDistribution: []
    });
    const [analyticsLoading, setAnalyticsLoading] = useState(true);
    const [analyticsError, setAnalyticsError] = useState("");
    const [error, setError] = useState("");
    const [search, setSearch] = useState("");
    const [searching, setSearching] = useState(false);
    const [page, setPage] = useState(0);
    const [pageSize] = useState(5);
    const [successMessage, setSuccessMessage] = useState("");
    const [sortBy, setSortBy] = useState("id");
    const [sortDir, setSortDir] = useState("asc");
    const cancelButtonRef = useRef<HTMLButtonElement>(null);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [showEmployeeForm, setShowEmployeeForm] = useState(false);
    const [employeeToDelete, setEmployeeToDelete] = useState<Employee | null>(null);
    const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
    const [deletingId, setDeletingId] = useState<number | null>(null);
    const debouncedSearch = useDebounce(search, 400);
    const [refreshing, setRefreshing] = useState(false);
    const [salaryOverview, setSalaryOverview] = useState<SalaryOverview[]>([]);

    const fetchAnalytics = useCallback(async () => {
    try {
        setAnalyticsLoading(true);
        setAnalyticsError("");

        const [analyticsData, salaryData] = await Promise.all([
          getAnalyticsData(),
          getSalaryOverview()
        ]);

        setAnalytics(analyticsData);
        setSalaryOverview(salaryData);
    } catch (error) {
        console.error("Error fetching analytics:", error);
        setAnalyticsError("Unable to load analytics data.");
    } finally {
        setAnalyticsLoading(false);
    }
}, []);

useEffect(() => {
  fetchAnalytics();
}, [fetchAnalytics]);
    const handleEdit = (employee: Employee) => {
      setSelectedEmployee(employee);
      setShowEmployeeForm(true);
    }

    const handleDelete = async (employee: Employee) => {
      setEmployeeToDelete(employee);
      if(deletingId !== null) {
        return;
      }

      const confirmed = window.confirm(
        `Are you sure you want to delete ${employee.firstName} ${employee.lastName}?`
      )

      if (!confirmed) {
        return;
      }

      try {
        setDeletingId(employee.id!);
        setError("");
        setSuccessMessage("");
        await deleteEmployee(employee.id!);
        setSuccessMessage("Employee deleted successfully!");
        if (employees.length === 1 && page > 0) {
          setPage((previousPage) => previousPage - 1);
        } else {
          await fetchEmployees();
        }
        
      } catch (error) {
        console.error("Failed to delete employee:", error);
        setError("Failed to delete employee. Please try again.");
      } finally {
        setDeletingId(null);
      }
    };

    const confirmDelete = async () => {

      if(!employeeToDelete || deletingId !== null) {
        return;
      }

      const employee = employeeToDelete;

      try {
        setDeletingId(employee.id!);
        setError("");
        setSuccessMessage("");

        await deleteEmployee(employee.id!);

        setSuccessMessage("Employee deleted successfully");
        setEmployeeToDelete(null);

        if(employees.length === 1 && page > 0) {
          setPage((previousPage) => previousPage - 1);
        }  else {
          await fetchEmployees();
        }
      } catch(error) {
        console.error("Failed to delete employee:", error);
        setError("Failed to delete employee. Please try again.");
      } finally {
        setDeletingId(null);
      }
    }

    const handleSort = (field: string) => {
      if (sortBy === field) {
        setSortDir(sortDir === "asc" ? "desc" : "asc");
      } else {
        setSortBy(field);
        setSortDir("asc");
      }
      setPage(0);
    }

    const goToNextPage = () => {
      if (page < totalPages - 1) {
        setPage(page + 1);
      }
    };

    const goToPreviousPage = () => {
      if (page > 0) {
        setPage(page - 1);
      }
    }

    const startItem = totalElements === 0
      ? 0
      : page * pageSize + 1;

    const endItem = Math.min(
      (page + 1) * pageSize,
      totalElements
    );

    const pageNumbers = Array.from(
      { length: totalPages },
      (_, index) => index
    );

    useEffect(() => {
      setPage(0);
    }, [debouncedSearch, sortBy, sortDir]);

  const fetchEmployees = async () => {
    try {
        setError("");

        if (loading) {
          setLoading(true);
        } else {
          setRefreshing(true);
        }

        if (debouncedSearch) {
            setSearching(true);
        }

        const data = await getEmployees(
            debouncedSearch,
            page,
            pageSize,
            sortBy,
            sortDir
        );

        console.log("Employee API response:", data);

        setEmployees(data.content ?? []);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);

        if(data.totalPages > 0 && page >= data.totalPages) {
          setPage(data.totalPages - 1);
        }
    } catch (error) {
        console.error(error);
        setError("Failed to fetch employees");
    } finally {
        setLoading(false);
        setSearching(false);
        setRefreshing(false);
    }
};
useEffect(() => {
  if (employeeToDelete) {
    cancelButtonRef.current?.focus();
  }
}, [employeeToDelete]);
useEffect(() => {
  if (!employeeToDelete) {
    return;
  }
  const handleKeyDown = (event: KeyboardEvent) => {
    if (event.key === "Escape" && deletingId === null) {
      setEmployeeToDelete(null);
    }
  };

  window.addEventListener("keydown", handleKeyDown);

  return () => {
    window.removeEventListener("keydown", handleKeyDown);
  };
}, [employeeToDelete, deletingId]);

useEffect(() => {
    fetchEmployees();
}, [debouncedSearch, page, sortBy, sortDir]);
    if (loading) {
        return <h2>Loading employees...</h2>;
    }

    // useEffect(() => {
    //   if (!successMessage) {
    //     return;
    //   }

    //   const timer = setTimeout(() => {
    //     setSuccessMessage("");
    //   }, 3000);

    //   return () => clearTimeout(timer);
    // }, [successMessage]);
    return (
        <div className="app-layout">
          {/*Sidebar*/}
          <aside className="sidebar">
              <div className="sidebar-brand">
                <div className="brand-icon">E</div>
                <span>EmployeeHub</span>
              </div>
            <nav className="sidebar-nav">
              <p className="sidebar-label">MENU</p>
              <a href="#dashboard-overview" className="nav-item active">
                <span>▦</span>
                    Dashboard
              </a>
                <a href="#employee-list" className="nav-item">
                    <span>♙</span>
                    Employees
                </a>
            </nav>
             <div className="sidebar-footer">
                <span className="sidebar-status"></span>
                Employee Management System
            </div>
          </aside>
          <main className="employee-dashboard" id="dashboard-overview">
          <div className="dashboard-header">
          <div className="dashboard-title">
            <h1>Employee Management</h1>
            <p>Manage your employees and their information</p>
            </div>

          <div className="dashboard-actions">
            <div>
            <input 
            type="text"
            placeholder="Search employees..."
            className="search-input"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            />
            {search && (
              <button className="clear-search-button" onClick={() => setSearch("")}>
                  ×
              </button>
            )}
            </div>
            {searching && (
              <span className="searching-text">
                Searching...
              </span>
            )}
            {refreshing && !searching && (
              <span className="refreshing-text">
                Refreshing employees...
              </span>
            )}
            {isAdmin && (
            <button className="add-employee-button"
            onClick={() => setShowEmployeeForm(true)}>
              + Add Employee
            </button>
            )}
            <button 
            type="button"
            className="logout-button"
            onClick={onLogout}>
              Sign Out
            </button>
          </div>
          </div>
          {/* Dashboard Analytics */}
<section className="analytics-grid">
    <div className="analytics-card">
        <div className="analytics-icon employees-icon">♙</div>
        <div>
            <p>Total Employees</p>
            <h2>{analytics.totalEmployees}</h2>
            <span>Across all pages</span>
        </div>
    </div>

    <div className="analytics-card">
        <div className="analytics-icon department-icon">▦</div>
        <div>
            <p>Total Employees</p>
            <h2>{analytics.totalEmployees}</h2>
            <span>Currently displayed</span>
        </div>
    </div>

    <div className="analytics-card">
        <div className="analytics-icon team-icon">♧</div>
        <div>
            <p>Departments on This Page</p>
            <h2>{analytics.departmentDistribution.length}</h2>
            <span>Distinct departments</span>
        </div>
    </div>

    <div className="analytics-card">
        <div className="analytics-icon salary-icon">₹</div>
        <div>
            <p>Average Salary on This Page</p>
            <h2>
                ₹{analytics.averageSalary.toLocaleString("en-IN", {
                    maximumFractionDigits: 0
                })}
            </h2>
            <span>Current page only</span>
        </div>
    </div>
</section>
{analyticsLoading ? (
    <p>Loading analytics...</p>
) : analyticsError ? (
    <p className="error-message">{analyticsError}</p>
) : (
  <Suspense fallback={<p>Loading Charts...</p>}>
    <AnalyticsCharts
        departmentDistribution={analytics.departmentDistribution}
        salaryOverview={salaryOverview}
        />
        </Suspense>
)}
          {successMessage && (
            <div className="success-message" role="status">
              {successMessage}
              {/* console.log("Success messge state:", successMessage); */}
            </div>
          )}
          {error && (
            <div className="error-message" role="alert">
              <span>{error}</span>
              <button
                type="button"
                className="retry-button"
                onClick={fetchEmployees}
                disabled={loading || searching}
            >
                Retry
            </button>
              <button 
              type="button"
              className="error-button"
              onClick={() => setError("")}
              aria-label="Dismiss error">
                ×
              </button>
            </div>
          )}
          {error ? (
            <div className="empty-state">
              Unable to load employees. Please check your connection
              and click Retry.
            </div>
          ) : employees.length === 0 ? (
            <div className="empty-state">
              No employees found.
            </div>
          ) : (
            <EmployeeTable 
            employees={employees} 
            sortBy={sortBy} 
            sortDir={sortDir} 
            onSort={handleSort}
            onEdit={handleEdit}
            onDelete={handleDelete}
            deletingId={deletingId}
            isAdmin={isAdmin}/>
          )
          }

            <div className="pagination-container">
              <div className="pagination-info">
                Showing {startItem}-{endItem} of {totalElements} employees
              </div>
              <div className="pagination-controls">
                <button 
                className="pagination-button" 
                onClick={goToPreviousPage} 
                disabled={page === 0}>
                     ← Previous
                </button>

                <div className="page-numbers">
                  {pageNumbers.map((pageNumber) => (
                    <button
                    key={pageNumber}
                    className={`page-number ${
                      page === pageNumber ? "active" : ""
                    }`}
                    onClick={() => setPage(pageNumber)}>
                        {pageNumber + 1}
                  </button>
                  ))}
                </div>

                <span className="pagination-page">
                  Page {page + 1} of {totalPages}
                </span>

                <button 
                className="pagination-button" 
                onClick={goToNextPage} 
                disabled={page >= totalPages - 1}>
                   Next →
                </button>
              </div>
            </div>
            {showEmployeeForm && (
            <EmployeeForm
    employee={selectedEmployee ?? undefined}
    onClose={() => {
        setShowEmployeeForm(false);
        setSelectedEmployee(null);
    }}
    onSuccess={async (employee) => {
      console.log("onSuccess called:", employee);
  setSuccessMessage(
    selectedEmployee 
    ? "Employee updated successfully!"
    : "Employee created successfully!"
  );
  setError("");
  setShowEmployeeForm(false);
  setSelectedEmployee(null);

  await fetchEmployees();
    }}
/>
            )}
            
{employeeToDelete && (
    <div className="modal-overlay">
        <div
            className="delete-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="delete-modal-title"
        >
            <h2 id="delete-modal-title">Delete Employee?</h2>

            <p>
                Are you sure you want to delete{" "}
                <strong>
                    {employeeToDelete.firstName}{" "}
                    {employeeToDelete.lastName}
                </strong>
                ? This action cannot be undone.
            </p>

            <div className="delete-modal-actions">
                <button
                    ref={cancelButtonRef}
                    type="button"
                    className="cancel-button"
                    onClick={() => setEmployeeToDelete(null)}
                    disabled={deletingId !== null}
                >
                    Cancel
                </button>

                <button
                    type="button"
                    className="confirm-delete-button"
                    onClick={confirmDelete}
                    disabled={deletingId !== null}
                >
                    {deletingId === employeeToDelete.id
                        ? "Deleting..."
                        : "Delete Employee"}
                </button>
            </div>
        </div>
    </div>
)}

        </main>
        </div>
    );
}

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    () => Boolean(localStorage.getItem("token"))
  );

  const [userRole, setUserRole] = useState<string | null>(
    () => getUserRole()
  );

  const handleLogin = () => {
    setUserRole(getUserRole());
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setUserRole(null);
    setIsAuthenticated(false);
  };

  if (!isAuthenticated) {
    return (
      <Login 
      onLogin={handleLogin}
      />
    );
  }
  return <EmployeeDashboard onLogout={handleLogout} isAdmin={userRole === "ADMIN"}/>
}
export default App;