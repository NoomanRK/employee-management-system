
export interface DepartmentDistribution {
    department: string;
    employeeCount: number;
}

export interface AnalyticsData {
    totalEmployees: number;
    averageSalary: number;
    departmentDistribution: DepartmentDistribution[];
}

export interface SalaryOverview {
    employeeName: string;
    salary: number;
}