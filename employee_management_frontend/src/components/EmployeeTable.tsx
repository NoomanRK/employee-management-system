
import type { Employee } from "../types/employee";
import "./EmployeeTable.css";

interface EmployeeTableProps {
    employees: Employee[];
    sortBy: string;
    sortDir: string;
    onSort: (field: string) => void;
    onEdit: (employee: Employee) => void;
    onDelete: (employee: Employee) => void;
    deletingId: number | null;
    isAdmin: boolean;
}

function EmployeeTable({
    employees,
    sortBy,
    sortDir,
    onSort,
    onEdit,
    onDelete,
    deletingId,
    isAdmin
}: EmployeeTableProps) {

    const getSortArrow = (field: string) => {
        if (sortBy !== field) return "";
        return sortDir === "asc" ? " ↑" : " ↓";
    };

    return (
        <div className="employee-table-container">
            <table className="employee-table">
                <thead>
                    <tr>
                        <th onClick={() => onSort("id")}>
                            ID{getSortArrow("id")}
                        </th>

                        <th onClick={() => onSort("firstName")}>
                            Name{getSortArrow("firstName")}
                        </th>

                        <th onClick={() => onSort("email")}>
                            Email{getSortArrow("email")}
                        </th>

                        <th onClick={() => onSort("phone")}>
                            Phone{getSortArrow("phone")}
                        </th>

                        <th onClick={() => onSort("department")}>
                            Department{getSortArrow("department")}
                        </th>

                        <th onClick={() => onSort("designation")}>
                            Designation{getSortArrow("designation")}
                        </th>

                        <th onClick={() => onSort("salary")}>
                            Salary{getSortArrow("salary")}
                        </th>

                        {isAdmin && <th>Actions</th>}
                    </tr>
                </thead>

                <tbody>
                    {employees.map((employee) => (
                        <tr key={employee.id}>
                            <td>{employee.id}</td>

                            <td>
                                {employee.firstName} {employee.lastName}
                            </td>

                            <td>{employee.email}</td>

                            <td>{employee.phone}</td>

                            <td>{employee.department}</td>

                            <td>{employee.designation}</td>

                            <td>
                                ₹{Number(employee.salary).toLocaleString("en-IN")}
                            </td>

                            {isAdmin && (
                                <td className="action-buttons">
                                    <button
                                        className="edit-button"
                                        onClick={() => onEdit(employee)}
                                    >
                                        Edit
                                    </button>

                                    <button
                                        className="delete-button"
                                        onClick={() => onDelete(employee)}
                                        disabled={deletingId !== null}
                                    >
                                        {deletingId === employee.id
                                            ? "Deleting..."
                                            : "Delete"}
                                    </button>
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default EmployeeTable;
