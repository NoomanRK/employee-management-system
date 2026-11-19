import { beforeEach, describe, expect, it } from "vitest";
import type { Employee } from "../types/employee";
import MockAdapter from "axios-mock-adapter";
import { createEmployee, deleteEmployee, getEmployees, updateEmployee } from "../services/employeeService";
import api from "../services/api";

const mockAxios = new MockAdapter(api);

describe("employeeService", () => {
    beforeEach(() => {
        mockAxios.reset();
    });

    it("fetches employees with the correct query parameters", async () => {
        const mockEmployees: Employee[] = [
 {      
                id: 1,
                firstName: "Nooman",
                lastName: "Khan",
                email: "nooman@example.com",
                phone: "9876543210",
                department: "IT",
                designation: "Developer",
                salary: 800000,
                isAdmin: false
            },
        ];

        mockAxios.onGet("/employees"
        ).reply(200, {
            data: {
                content: mockEmployees,
                pageNumber: 0,
                pageSize: 5,
                totalElements: 1,
                totalPages: 1,
            },
        });

        const result = await getEmployees(
            "Nooman",
            0,
            5,
            "id",
            "asc"
        );
        expect(result.content).toEqual(mockEmployees);
        expect(result.totalElements).toBe(1);
        expect(mockAxios.history.get).toHaveLength(1);

        expect(mockAxios.history.get[0].params).toEqual({
            search: "Nooman",
            page: 0,
            size: 5,
            sortBy: "id",
            sortDir: "asc",
        });
    });

    
it("creates an employee with the correct request payload", async () => {
    const newEmployee: Employee = {
        firstName: "Amit",
        lastName: "Sharma",
        email: "amit@example.com",
        phone: "9876543210",
        department: "IT",
        designation: "Developer",
        salary: 600000,
        isAdmin: false
    };

    mockAxios.onPost(
        "http://localhost:8080/api/employees"
    ).reply(201, {
        data: {
            id: 2,
            ...newEmployee,
        },
    });

    const result = await createEmployee(newEmployee);

    expect(mockAxios.history.post).toHaveLength(1);

    expect(JSON.parse(mockAxios.history.post[0].data)).toEqual(
        newEmployee
    );

    expect(result).toEqual({
        id: 2,
        ...newEmployee,
    });
});

it("updates an employee with the correct ID and payload", async () => {
    const updatedEmployee: Employee = {
        firstName: "Amit",
        lastName: "Sharma",
        email: "amit@gmail.com",
        phone: "9876543210",
        department: "IT",
        designation: "Senior Developer",
        salary: 700000,
        isAdmin: false
    };

    mockAxios.onPut(
        "http://localhost:8080/api/employees/1"
    ).reply(200, {
        data: {
            id: 1,
            ...updatedEmployee,
        },
    });

    await updateEmployee(1, updatedEmployee);

    expect(mockAxios.history.put).toHaveLength(1);
    expect(mockAxios.history.put[0].url).toBe(
        "http://localhost:8080/api/employees/1"
    );
    expect(JSON.parse(mockAxios.history.put[0].data)).toEqual(
        updatedEmployee
    );
});

it("deletes an employee using the correct ID", async () => {
    mockAxios.onDelete(
        "http://localhost:8080/api/employees/1"
    ).reply(204);

    await deleteEmployee(1);

    expect(mockAxios.history.delete).toHaveLength(1);
    expect(mockAxios.history.delete[0].url).toBe(
        "http://localhost:8080/api/employees/1"
    );
});


it("throws an error when fetching employees fails", async () => {
    mockAxios.onGet(
        "http://localhost:8080/api/employees"
    ).reply(500, {
        message: "Internal Server Error",
    });

    await expect(
        getEmployees("Nooman", 0, 5, "id", "asc")
    ).rejects.toThrow();
});


});