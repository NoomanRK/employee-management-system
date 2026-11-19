
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi } from "vitest";
import EmployeeTable from "./EmployeeTable";
import type { Employee } from "../types/employee";


const mockEmployees: Employee[] = [
    {
        id: 1,
        firstName: "Nooman",
        lastName: "Khan",
        email: "nooman.employee@gmail.com",
        phone: "7978863079",
        department: "IT",
        designation: "Java Developer",
        salary: 8000000,
        isAdmin: true,    },
];

describe("EmployeeTable", () => {
    it("renders employee details correctly", () => {
        render(
            <EmployeeTable
                employees={mockEmployees}
                sortBy="id"
                sortDir="asc"
                onSort={vi.fn()}
                onEdit={vi.fn()}
                onDelete={vi.fn()}
                deletingId={null}
                isAdmin={true}
            />
        );

        
        expect(screen.getByText("NoomanKhan")).toBeInTheDocument();
        expect(
            screen.getByText("nooman.employee@gmail.com")
        ).toBeInTheDocument();
        expect(screen.getByText("Java Developer")).toBeInTheDocument();
    });
    it("calls onEdit when the Edit button is clicked", async () => {
        const user = userEvent.setup();
        const onEdit = vi.fn();
    
        render(
            <EmployeeTable
                employees={mockEmployees}
                sortBy="id"
                sortDir="asc"
                onSort={vi.fn()}
                onEdit={onEdit}
                onDelete={vi.fn()}
                deletingId={null}
                isAdmin={true}
            />
        );
        const editButton = screen.getByRole("button", { name: "Edit",});
        expect(editButton).toBeInTheDocument();
        expect(editButton).toBeEnabled();
        await user.click(editButton);
        // console.log("onEdit calls:", onEdit.mock.calls);
        
        expect(onEdit).toHaveBeenCalledTimes(1);
        expect(onEdit).toHaveBeenCalledWith(mockEmployees[0]);
    })

    it("calls onDelete with the correct employee when Delete is clicked", async () => {
        const user = userEvent.setup();
        const onDelete = vi.fn();

        render(
            <EmployeeTable
            employees={mockEmployees}
            sortBy="id"
            sortDir="asc"
            onSort={vi.fn()}
            onEdit={vi.fn()}
            onDelete={onDelete}
            deletingId={null}
            isAdmin={true}
            />
        );
        await user.click(
            screen.getByRole("button", {name: "Delete" })
        );
        expect(onDelete).toHaveBeenCalledTimes(1);
        expect(onDelete).toHaveBeenCalledWith(mockEmployees[0]);
    })

    it("disables the Delete button and shows Deleting while deletion is in progress", () => {
        render(
            <EmployeeTable
            employees={mockEmployees}
            sortBy="id"
            sortDir="asc"
            onSort={vi.fn()}
            onEdit={vi.fn()}
            onDelete={vi.fn()}
            deletingId={1}
            isAdmin={true}/>
        );

        const deleteButton = screen.getByRole("button", {
            name: "Deleting...",
        });
        expect(deleteButton).toBeDisabled();
    })

    it("calls onSort with the correct field when a column header is clicked", async () => {
        const user = userEvent.setup();
        const onSort = vi.fn();

        render(
            <EmployeeTable
            employees={mockEmployees}
            sortBy="id"
            sortDir="asc"
            onSort={onSort}
            onEdit={vi.fn()}
            onDelete={vi.fn()}
            deletingId={null}
            isAdmin={true}/>
        );

        await user.click(
            screen.getByRole("columnheader", {name: /Department/})
        )
        expect(onSort).toHaveBeenCalledTimes(1);
        expect(onSort).toHaveBeenCalledWith("department")
    })
});
