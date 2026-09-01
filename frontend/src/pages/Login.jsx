import { useState } from "react";
import axios from "axios";
import { ShieldCheck } from "lucide-react";
import { useNavigate } from "react-router-dom";

function Login() {
    const navigate = useNavigate();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleLogin = async (event) => {
        event.preventDefault();

        setError("");
        setLoading(true);

        try {
            const response = await axios.post(
                "http://localhost:8080/api/v1/auth/login",
                {
                    username,
                    password,
                }
            );

            const loginData = response.data.data;

            localStorage.setItem("token", loginData.token);
            localStorage.setItem("username", loginData.username);
            localStorage.setItem("role", loginData.role);

            navigate("/dashboard");
        } catch (error) {
            if (error.response) {
                setError(
                    error.response.data?.message ||
                    "Invalid username or password."
                );
            } else {
                setError(
                    "Unable to connect to JewelCore server."
                );
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="login-page">

            {/* Left visual section */}
            <section className="login-visual">

                <div className="visual-content">

                    <div className="brand">
                        <div className="brand-mark">
                            J
                        </div>

                        <span className="brand-name">
                            JEWELCORE
                        </span>
                    </div>

                    <div className="visual-message">

                        <div className="visual-eyebrow">
                            Jewellery Management
                        </div>

                        <h1>
                            Where every piece
                            <br />
                            <span>has its value.</span>
                        </h1>

                        <p>
                            A centralized workspace for managing
                            jewellery, inventory, metal rates and
                            pricing with precision.
                        </p>

                    </div>

                    <div className="visual-footer">
                        <span>JEWELCORE SYSTEM</span>
                        <span>EST. 2026</span>
                    </div>

                </div>

            </section>

            {/* Login section */}
            <section className="login-panel">

                <div className="login-container">

                    <div className="login-heading">

                        <div className="eyebrow">
                            Secure Access
                        </div>

                        <h2>
                            Welcome back.
                        </h2>

                        <p>
                            Sign in to access your JewelCore workspace.
                        </p>

                    </div>

                    <form
                        className="login-form"
                        onSubmit={handleLogin}
                    >

                        <div className="form-group">

                            <label htmlFor="username">
                                Username
                            </label>

                            <input
                                id="username"
                                type="text"
                                value={username}
                                onChange={(event) =>
                                    setUsername(event.target.value)
                                }
                                placeholder="Enter your username"
                                autoComplete="username"
                                required
                            />

                        </div>

                        <div className="form-group">

                            <label htmlFor="password">
                                Password
                            </label>

                            <input
                                id="password"
                                type="password"
                                value={password}
                                onChange={(event) =>
                                    setPassword(event.target.value)
                                }
                                placeholder="Enter your password"
                                autoComplete="current-password"
                                required
                            />

                        </div>

                        {error && (
                            <div className="login-error">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="login-button"
                            disabled={loading}
                        >
                            {loading
                                ? "Signing in..."
                                : "Sign in"}
                        </button>

                    </form>

                    <div className="login-security">

                        <ShieldCheck
                            size={15}
                            strokeWidth={1.5}
                            style={{
                                verticalAlign: "middle",
                                marginRight: "7px",
                            }}
                        />

                        Protected by JewelCore authentication
                    </div>

                </div>

            </section>

        </main>
    );
}

export default Login;