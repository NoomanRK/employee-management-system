import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi, beforeEach } from "vitest";
import EmployeeForm from "./EmployeeForm";
import { render, screen } from "@testing-library/react";
import { createEmployee, updateEmployee } from "../services/employeeService";
vi.mock("../services/employeeService", () => ({
    createEmployee: vi.fn(),
    updateEmployee: vi.fn(),
}));

describe("EmployeeForm", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    })
    it("shows validation errors when submitted empty", async () => {
        const user = userEvent.setup();

        render(
            <EmployeeForm
            onClose={vi.fn()}
            onSuccess={vi.fn()}/>
        );

        await user.click(screen.getByRole("button", { name: "Save Employee",})
        );

        expect(screen.getByText("First name is required")).toBeInTheDocument();
        expect(screen.getByText("Last name is required")).toBeInTheDocument();
        expect(screen.getByText("Email is reqired")).toBeInTheDocument();
        expect(screen.getByText("Phone number is required")).toBeInTheDocument();
        expect(screen.getByText("Department is required")).toBeInTheDocument();
        expect(screen.getByText("Designation is required")).toBeInTheDocument();

        expect(createEmployee).not.toHaveBeenCalled();
    });

    it("shows an error when the phone number has fewer than 10 digits", async () => {
        const user = userEvent.setup();

        render(
            <EmployeeForm
            onClose={vi.fn()}
            onSuccess={vi.fn()}
            />
        );
        await user.type(
            screen.getByPlaceholderText("Enter first name"),
            "Nooman"
        );
        await user.type(
            screen.getByPlaceholderText("Enter last name"),
            "Khan"
        )
        await user.type(
        screen.getByPlaceholderText("Enter email"),
        "nooman@example.com"
    );
    await user.type(
        screen.getByPlaceholderText("Enter 10 digit phone number"),
        "12345"
    );
    await user.type(
        screen.getByPlaceholderText("Enter department"),
        "IT"
    );
    await user.type(
        screen.getByPlaceholderText("Enter designation"),
        "Developer"
    );
    
    await user.click(
        screen.getByRole("button", {
            name: "Save Employee",
        })
    );
   
    const phoneInput = screen.getByPlaceholderText(
        "Enter 10 digit phone number"
    );
    expect(phoneInput).toHaveValue("12345");
    expect(createEmployee).not.toHaveBeenCalled();
    expect(
        screen.getByText(
            "Phone number must contain exactly 10 digits"
        )
        ).toBeInTheDocument();

        expect(createEmployee).not.toHaveBeenCalled();
    })

    it("creates an employee when the form is valid", async () => {
        const user = userEvent.setup();

        const mockEmployee = {
            id: 1,
            firstName: "Nooman",
            lastName: "Khan",
            email: "nooman@example.com",
        phone: "9876543210",
        department: "IT",
        designation: "Developer",
        salary: 50000,
        isAdmin: false
        }
        vi.mocked(createEmployee).mockResolvedValueOnce(mockEmployee);

        const onSuccess = vi.fn();

        render(
            <EmployeeForm
            onClose={vi.fn()}
            onSuccess={onSuccess}
            />
        )

        await user.type(
            screen.getByPlaceholderText("Enter first name"),
            "Nooman"
        );
        await user.type(
            screen.getByPlaceholderText("Enter last name"),
            "Khan"
        );
         await user.type(
        screen.getByPlaceholderText("Enter email"),
        "nooman@example.com"
    );
    await user.type(
        screen.getByPlaceholderText("Enter 10 digit phone number"),
        "9876543210"
    );
    await user.type(
        screen.getByPlaceholderText("Enter department"),
        "IT"
    );
    await user.type(
        screen.getByPlaceholderText("Enter designation"),
        "Developer"
    );
    await user.clear(
        screen.getByPlaceholderText("Enter salary")
    );
    await user.type(
        screen.getByPlaceholderText("Enter salary"),
        "50000"
    );
     await user.click(
        screen.getByRole("button", { name: "Save Employee" })
    );

    expect(createEmployee).toHaveBeenCalledTimes(1);
    expect(createEmployee).toHaveBeenCalledWith({
        firstName: "Nooman",
        lastName: "Khan",
        email: "nooman@example.com",
        phone: "9876543210",
        department: "IT",
        designation: "Developer",
        salary: 50000,

    });
        expect(onSuccess).toHaveBeenCalledWith(mockEmployee);
    })

    it("updates an existing employee", async () => {
        const user = userEvent.setup();

        const employee = {
               id: 1,
        firstName: "Nooman",
        lastName: "Khan",
        email: "nooman@example.com",
        phone: "9876543210",
        department: "IT",
        designation: "Developer",
        salary: 50000,
        isAdmin: false
        }

        const updatedEmployee = {
            ...employee,
            designation: "Senior Developer",
        }
        vi.mocked(updateEmployee).mockResolvedValueOnce(updatedEmployee);

        const onSuccess = vi.fn();

        render(
             <EmployeeForm
            employee={employee}
            onClose={vi.fn()}
            onSuccess={onSuccess}
        />
        )

        const designationInput = screen.getByPlaceholderText(
            "Enter designation"
        );

        await user.clear(designationInput);
        await user.type(designationInput, "Senior Developer");

        await user.click(
            screen.getByRole("button", { name: "Update Employee" })
        );

        expect(updateEmployee).toHaveBeenCalledTimes(1);
        expect(updateEmployee).toHaveBeenCalledWith(1, {
            ...employee,
            designation: "Senior Developer",
        })

        expect(onSuccess).toHaveBeenCalledWith(updatedEmployee);
        expect(createEmployee).not.toHaveBeenCalled();
    })

    it("calls onClose when the Cancel button is clicked", async () => {
        const user = userEvent.setup();
        const onClose = vi.fn();

        render(
            <EmployeeForm
            onClose={onClose}
            onSuccess={vi.fn()}
            />
        );
        await user.click(
            screen.getByRole("button", { name: "Cancel" })
        )
        expect(onClose).toHaveBeenCalledTimes(1);
    });
     
    it("calls onClose when the close button is clicked", async () => {
        const user = userEvent.setup();
        const onClose = vi.fn();

        render(
            <EmployeeForm
            onClose={onClose}
            onSuccess={vi.fn()}
            />
        )

        await user.click(
            screen.getByRole("button", { name: "×" })
        );
        expect(onClose).toHaveBeenCalledTimes(1);
    });

    
it("displays a duplicate-email error when the API returns 409", async () => {
    const user = userEvent.setup();
    const onSuccess = vi.fn();

    vi.mocked(createEmployee).mockRejectedValueOnce({
        isAxiosError: true,
        status: 409,
        response: {
            status: 409,
            data: {
                message: "Email already exists. Please use a different email address.",
            },
        },
    });

    render(
        <EmployeeForm
            onClose={vi.fn()}
            onSuccess={onSuccess}
        />
    );

    await user.type(
        screen.getByPlaceholderText("Enter first name"),
        "Nooman"
    );
    await user.type(
        screen.getByPlaceholderText("Enter last name"),
        "Khan"
    );
    await user.type(
        screen.getByPlaceholderText("Enter email"),
        "nooman@example.com"
    );
    await user.type(
        screen.getByPlaceholderText("Enter 10 digit phone number"),
        "9876543210"
    );
    await user.type(
        screen.getByPlaceholderText("Enter department"),
        "IT"
    );
    await user.type(
        screen.getByPlaceholderText("Enter designation"),
        "Developer"
    );

    await user.click(
        screen.getByRole("button", { name: "Save Employee" })
    );

    expect(
        await screen.findByText(
            "Email already exists. Please use a different email address."
        )
    ).toBeInTheDocument();

    expect(onSuccess).not.toHaveBeenCalled();
});


it("displays field validation errors when the API returns 400", async () => {
    const user = userEvent.setup();

    vi.mocked(createEmployee).mockRejectedValueOnce({
        isAxiosError: true,
        status: 400,
        response: {
            status: 400,
            data: {
                phone: "Phone number must contain exactly 10 digits",
            },
        },
    });

    render(
        <EmployeeForm
            onClose={vi.fn()}
            onSuccess={vi.fn()}
        />
    );

    await user.type(
        screen.getByPlaceholderText("Enter first name"),
        "Nooman"
    );
    await user.type(
        screen.getByPlaceholderText("Enter last name"),
        "Khan"
    );
    await user.type(
        screen.getByPlaceholderText("Enter email"),
        "nooman@example.com"
    );
    await user.type(
        screen.getByPlaceholderText("Enter 10 digit phone number"),
        "9876543210"
    );
    await user.type(
        screen.getByPlaceholderText("Enter department"),
        "IT"
    );
    await user.type(
        screen.getByPlaceholderText("Enter designation"),
        "Developer"
    );

    await user.click(
        screen.getByRole("button", { name: "Save Employee" })
    );

    expect(
        await screen.findByText(
            "Phone number must contain exactly 10 digits"
        )
    ).toBeInTheDocument();
});


it("displays a generic error when the API returns 500", async () => {
    const user = userEvent.setup();

    vi.mocked(createEmployee).mockRejectedValueOnce({
        isAxiosError: true,
        status: 500,
        response: {
            status: 500,
            data: {
                message: "Internal Server Error",
            },
        },
    });

    render(
        <EmployeeForm
            onClose={vi.fn()}
            onSuccess={vi.fn()}
        />
    );

    await user.type(
        screen.getByPlaceholderText("Enter first name"),
        "Nooman"
    );
    await user.type(
        screen.getByPlaceholderText("Enter last name"),
        "Khan"
    );
    await user.type(
        screen.getByPlaceholderText("Enter email"),
        "nooman@example.com"
    );
    await user.type(
        screen.getByPlaceholderText("Enter 10 digit phone number"),
        "9876543210"
    );
    await user.type(
        screen.getByPlaceholderText("Enter department"),
        "IT"
    );
    await user.type(
        screen.getByPlaceholderText("Enter designation"),
        "Developer"
    );

    await user.click(
        screen.getByRole("button", { name: "Save Employee" })
    );

    expect(
        await screen.findByText(
            "Something went wrong. Please try again."
        )
    ).toBeInTheDocument();
});

 it("displays a connection error when server is unreachable", async () => {
    const user = userEvent.setup();

    vi.mocked(createEmployee).mockRejectedValueOnce({
        isAxiosError: true,
        message: "Network Error",
        response: undefined,
        status: undefined,
    })

    render(
        <EmployeeForm
        onClose={vi.fn()}
        onSuccess={vi.fn()}
        />
    )
    await user.type(
        screen.getByPlaceholderText("Enter first name"),
        "Nooman"
    )
    await user.type(
        screen.getByPlaceholderText("Enter last name"),
        "Khan"
    )

    await user.type(
        screen.getByPlaceholderText("Enter email"),
        "nooman@example.com"
    );
    await user.type(
        screen.getByPlaceholderText("Enter 10 digit phone number"),
        "9876543210"
    );
    await user.type(
        screen.getByPlaceholderText("Enter department"),
        "IT"
    );
    await user.type(
        screen.getByPlaceholderText("Enter designation"),
        "Developer"
    );
    await user.click(
        screen.getByRole("button", { name: "Save Employee" })
    );

    expect(
        await screen.findByText("Unable to connect to the server.")
    ).toBeInTheDocument();
 });


it("re-enables the Save button after an API error", async () => {
    const user = userEvent.setup();

    vi.mocked(createEmployee).mockRejectedValueOnce({
        isAxiosError: true,
        status: 500,
        response: {
            status: 500,
            data: {},
        },
    });

    render(
        <EmployeeForm
            onClose={vi.fn()}
            onSuccess={vi.fn()}
        />
    );

    await user.type(
        screen.getByPlaceholderText("Enter first name"),
        "Nooman"
    );
    await user.type(
        screen.getByPlaceholderText("Enter last name"),
        "Khan"
    );
    await user.type(
        screen.getByPlaceholderText("Enter email"),
        "nooman@example.com"
    );
    await user.type(
        screen.getByPlaceholderText("Enter 10 digit phone number"),
        "9876543210"
    );
    await user.type(
        screen.getByPlaceholderText("Enter department"),
        "IT"
    );
    await user.type(
        screen.getByPlaceholderText("Enter designation"),
        "Developer"
    );

    const saveButton = screen.getByRole("button", {
        name: "Save Employee",
    });

    await user.click(saveButton);

    expect(
        await screen.findByText(
            "Something went wrong. Please try again."
        )
    ).toBeInTheDocument();

    expect(saveButton).toBeEnabled();
});


})