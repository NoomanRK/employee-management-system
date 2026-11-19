import { useState } from "react";
import type { AuthRequest } from "../types/auth";
import { loginUser } from "../services/authService";
import "./Auth.css";

function Login({onLogin }: { onLogin: () => void }) {
    const [formData, setFormData] = useState<AuthRequest>({
        email: "",
        password: "",
    });

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement>
    ) => {
        const { name, value } = e.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (
        e: React.FormEvent<HTMLFormElement>
    ) => {
        e.preventDefault();

        setError("");
        setLoading(true);

        try {
            const response = await loginUser(formData);
            localStorage.setItem("token", response.token);
            onLogin();
        } catch (err: any) {
            setError(
                err.response?.data?.message ||
                "Invalid email or password."
            );
        } finally {
            setLoading(false);
        }
    };

    return(
        <div className="auth-container">
            <form className="auth-form" onSubmit={handleSubmit}>
                <h2>Login</h2>

                {error && (
                    <p className="auth-error">{error}</p>
                )}

                <div className="form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        name="email"
                        placeholder="Enter your email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Password</label>
                    <input
                        type="password"
                        name="password"
                        placeholder="Enter your password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                </div>

               <button type="submit" disabled={loading}>
                {loading ? "Logging in..." : "Login"}
                </button> 
            </form>
        </div>
    );
}

export default Login;