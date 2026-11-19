import type { Employee } from "../types/employee";
import api from "./api";


export interface EmployeePage {
    content: Employee[];
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
}



export const getEmployees = async (
    search: string = "",
    page: number = 0,
    size: number = 5,
    sortBy: string = "id",
    sortDir: string = "asc"
): Promise<EmployeePage> => {

    const response = await api.get("/employees", {
        params: {
            search: search || undefined,
            page,
            size,
            sortBy,
            sortDir
        }
    });

    return response.data.data;
}

export const createEmployee = async (
    employee: Employee
): Promise<Employee> => {
    const response = await api.post("/employees", employee);
    return response.data.data
}

export const updateEmployee = async (
    id: number,
    employee: Employee
): Promise<Employee> => {
    const response = await api.put(
        `employees/${id}`,
        employee
    );

    return response.data.data;
}

export const deleteEmployee = async (
    id: number
): Promise<void> => {
    await api.delete(`/employees/${id}`);
};