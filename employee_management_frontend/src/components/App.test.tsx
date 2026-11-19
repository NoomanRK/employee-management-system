import { beforeEach, describe, expect, it, vi } from "vitest";
import { getEmployees } from "../services/employeeService";
import type { Employee } from "../types/employee";
import App from "../App";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

vi.mock("../services/employeeService", () => ({
    getEmployees: vi.fn(),
    createEmployee: vi.fn(),
    updateEmployee: vi.fn(),
    deleteEmployee: vi.fn(),
}));

const mockEmployee: Employee = {
     id: 1,
    firstName: "Nooman",
    lastName: "Khan",
    email: "nooman@example.com",
    phone: "9876543210",
    department: "IT",
    designation: "Developer",
    salary: 800000,
    isAdmin: false
}

describe("App dashboard", () => {
    beforeEach(() => {
        vi.clearAllMocks();

        vi.mocked(getEmployees).mockResolvedValue({
            content: [mockEmployee],
            pageNumber: 0,
            pageSize: 5,
            totalElements: 1,
            totalPages: 1,
        });
    });

    it("displays employees after successfully loading the dashboard", async () => {
        render(<App/>)

        expect(
            await screen.findByText("nooman@example.com")
        ).toBeInTheDocument();

        expect(getEmployees).toHaveBeenCalled();
    });

    it("fetches employees when a search term is entered", async () => {
        const user = userEvent.setup();

        render(<App/>);

        await screen.findByText("nooman@example.com");

        await user.type(
            screen.getByPlaceholderText(/search/i),
            "Amit"
        );

        await waitFor(() => {
            expect(getEmployees).toHaveBeenCalledWith(
            "Amit",
            0,
            5,
            "id",
            "asc"
        );
    });
});

    it("displays an error message when employee loading fails", async () => {
        vi.mocked(getEmployees).mockRejectedValueOnce(
            new Error("Unable to connect to the server.")
        );

        render(<App />);

        expect(
            await screen.findByText("Failed to fetch employees")
        ).toBeInTheDocument();
    });
})