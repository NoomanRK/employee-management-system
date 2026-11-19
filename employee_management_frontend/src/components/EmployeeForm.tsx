import { useState } from "react";
import type { Employee } from "../types/employee";
import { createEmployee, updateEmployee } from "../services/employeeService";
import './EmployeeDashboard.css'
import axios from "axios";
interface EmployeeFormProps {
    employee?: Employee;
    onClose: () => void;
    onSuccess: (employee: Employee) => void;
}

function EmployeeForm({ employee, onClose, onSuccess }: EmployeeFormProps) {

    const [errors, setErrors] = useState<Record<string, string>>({});
    const [saving, setSaving] = useState(false);
    const [serverError, setServerError] = useState("");

    const [formData, setFormData] = useState<Employee>(
        employee ?? { 
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        department: "",
        designation: "",
        salary: 0,
        isAdmin: false
        
    });

    const handleChange = (
        event: React.ChangeEvent<HTMLInputElement>
    ) => {
        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: name === "salary" ? Number(value) : value,
        }));

        setErrors((previous) => ({
            ...previous,
            [name]: "",
        }));

        setServerError("");
    };

    const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (saving) {
        return;
    }

    setServerError("");

    if (!validateForm()) {
        return;
    }

    try {
        setSaving(true);
        let savedEmployee: Employee;
        if(employee?.id) {
            savedEmployee = await updateEmployee(
                employee.id,
                formData
            );
        } else {
            savedEmployee = await createEmployee(formData);
        }
        onSuccess(savedEmployee);
    } catch (error: unknown) {
        console.error("Failed to create employee:", error);
        if (axios.isAxiosError(error)) {
            const status = error?.status;
            const responseData = error.response?.data;
            if(status === 409) {
                setServerError(
                    responseData?.message || 
                    "Email already exists. Please use a different email address."
                );
            } else if (status === 400) {
                if (
                    responseData && 
                    typeof responseData === "object" &&
                    !Array.isArray(responseData)
                ) {
                    setErrors(responseData);
                } else {
                    setServerError(
                        responseData?.message || 
                        "Please check the entered information."
                    );

                }
            } else if (status) {
                setServerError(
                    "Something went wrong. Please try again."
                )
            } else {
                setServerError(
                    "Unable to connect to the server."
                );
            }
        } else {
            setServerError("Something went wrong. Please try again.");
        }
    } finally {
        setSaving(false);
    }
};

    const validateForm = () => {
        const newErrors: Record<string, string> = {};
        
        if(!formData.firstName.trim()) {
            newErrors.firstName = "First name is required";
        }

        if(!formData.lastName.trim()) {
            newErrors.lastName = "Last name is required";
        }

        if(!formData.email.trim()) {
            newErrors.email = "Email is reqired";
        } else if (
            !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)
        ) {
            newErrors.email = "Please provide a valid email address";
        }

        if(!formData.phone.trim()) {
            newErrors.phone = "Phone number is required";
        } else if (!/^[0-9]{10}$/.test(formData.phone)) {
            newErrors.phone = "Phone number must contain exactly 10 digits";
        }

        if(!formData.department.trim()) {
            newErrors.department = "Department is required";
        }

        if(!formData.designation.trim()) {
            newErrors.designation = "Designation is required";
        }

        if(formData.salary < 0) {
            newErrors.salary = "Salary cannot be negative"
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    }


    return (
        <div className="employee-form-overlay">
            <div className="employee-form-modal">
                <div className="employee-form-header">
                    <div>
                        <h2>{employee ? "Edit Employee" : "Add Employee"}</h2>
                        <p>{employee ?
                        "Update employee information"
                        :"Enter employee information"}</p>
                    </div>

                    <button
                    type="button"
                    className="close-button"
                    onClick={onClose}>
                         ×
                    </button>
                </div>
                {serverError && (
                    <div className="server-error">
                        {serverError}
                    </div>
                )}
                <form onSubmit={handleSubmit}>
                    <div>
                        <div className="form-group">
                            <label>First Name</label>
                            <input 
                            type="text"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            placeholder="Enter first name"
                            />
                            {errors.firstName && (
                                <span className="field-error">
                                    {errors.firstName}
                                </span>
                            )}
                        </div>
                        <div className="form-group">
                            <label>Last Name</label>
                            <input type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            placeholder="Enter last name"
                            />
                            {errors.lastName && (
                                <span className="field-error">
                                    {errors.lastName}
                                </span>
                            )}
                        </div>
                        <div className="form-group">
                            <label>Email</label>
                            <input type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Enter email"
                            />
                            {errors.email && (
                                <span className="field-error">
                                    {errors.email}
                                </span>
                            )}
                        </div>
                            <div className="form-group">
                            <label>Phone</label>
                            <input type="text"
                            name="phone"
                            value={formData.phone}
                            onChange={handleChange}
                            placeholder="Enter 10 digit phone number"
                            />
                            {errors.phone && (
                                <span className="field-error">
                                    {errors.phone}
                                </span>
                            )}
                        </div>
                            <div className="form-group">
                            <label>Department</label>
                            <input type="text"
                            name="department"
                            value={formData.department}
                            onChange={handleChange}
                            placeholder="Enter department"
                            />
                            {errors.department && (
                                <span className="field-error">
                                    {errors.department}
                                </span>
                            )}
                        </div>
                        <div className="form-group">
                            <label>Designation</label>
                            <input type="text"
                            name="designation"
                            value={formData.designation}
                            onChange={handleChange}
                            placeholder="Enter designation"
                            />
                            {errors.designation && (
                                <span className="field-error">
                                    {errors.designation}
                                </span>
                            )}
                        </div>
                        <div className="form-group">
                            <label>Salary</label>
                            <input type="number"
                            name="salary"
                            value={formData.salary}
                            onChange={handleChange}
                            placeholder="Enter salary"
                            min="0"
                            />
                            {errors.salary && (
                                <span className="field-error">
                                    {errors.salary}
                                </span>
                            )}
                        </div>
                        <div className="employee-form-actions">
                            <button 
                            type="button"
                            className="cancel-button"
                            onClick={onClose}>
                                Cancel
                            </button>
                            <button
                            type="submit"
                            className="save-button"
                            disabled={saving}>
                                {saving 
                                ? "Saving..." 
                                : employee 
                                ? "Update Employee"
                                : "Save Employee"}
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}
export default EmployeeForm;