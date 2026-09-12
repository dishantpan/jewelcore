import {
    LayoutDashboard,
    Gem,
    Boxes,
    Tags,
    CircleDollarSign,
    Percent,
    TrendingUp,
    Calculator,
    Receipt,
    BarChart3,
    Users,
    LogOut,
} from "lucide-react";
import "./DesktopSidebar.css";
import { useLocation, useNavigate } from "react-router-dom";

function DesktopSidebar() {

    const navigate = useNavigate();
    const location = useLocation();

    const username =
        localStorage.getItem("username") || "User";

    const role =
        localStorage.getItem("role") || "USER";

    const isOwner = role === "OWNER";

    const navigation = [
        {
            label: "Overview",
            path: "/dashboard",
            icon: LayoutDashboard,
        },
        {
            label: "Jewellery",
            path: "/jewellery",
            icon: Gem,
        },
        {
            label: "Inventory",
            path: "/inventory",
            icon: Boxes,
        },
        {
            label: "Categories",
            path: "/categories",
            icon: Tags,
            role: "OWNER",
        },
        {
            label: "Metals",
            path: "/metals",
            icon: CircleDollarSign,
            role: "OWNER",
        },
        {
            label: "Purities",
            path: "/purities",
            icon: Percent,
            role: "OWNER",
        },
        {
            label: "Metal Prices",
            path: "/metal-prices",
            icon: TrendingUp,
            role: "OWNER",
        },
        {
            label: "Pricing",
            path: "/pricing",
            icon: Calculator,
        },
        {
            label: "Billing",
            path: "/billing",
            icon: Receipt,
        },
        {
            label: "Reports",
            path: "/reports",
            icon: BarChart3,
            role: "OWNER",
        },
        {
            label: "Users",
            path: "/users",
            icon: Users,
            role: "OWNER",
        },
    ];

    const filteredNavigation = navigation.filter(item => !item.role || isOwner);

    const handleNavigation = (path) => {
        navigate(path);
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        localStorage.removeItem("role");

        navigate("/login", {
            replace: true,
        });
    };

    const isActive = (path) => {
        if (path === "/dashboard") {
            return location.pathname === "/dashboard";
        }

        return location.pathname.startsWith(path);
    };

    return (
        <aside className="desktop-sidebar">

            {/* BRAND */}

            <div className="desktop-sidebar-brand">

                <div className="desktop-brand-mark">
                    J
                </div>

                <div className="desktop-brand-text">

                    <strong>
                        JEWELCORE
                    </strong>

                    <span>
                        JEWELLERY MANAGEMENT
                    </span>

                </div>

            </div>


            {/* NAVIGATION */}

            <div className="desktop-sidebar-section-label">
                Workspace
            </div>

            <nav className="desktop-sidebar-nav">

                {filteredNavigation.map((item) => {

                    const Icon = item.icon;

                    return (
                        <button
                            key={item.path}
                            type="button"
                            className={`desktop-nav-item ${
                                isActive(item.path)
                                    ? "active"
                                    : ""
                            }`}
                            onClick={() =>
                                handleNavigation(item.path)
                            }
                        >

                            <Icon
                                size={17}
                                strokeWidth={1.6}
                            />

                            <span>
                                {item.label}
                            </span>

                        </button>
                    );
                })}

            </nav>


            {/* USER AREA */}

            <div className="desktop-sidebar-bottom">

                <div className="desktop-user">

                    <div className="desktop-user-avatar">
                        {username.charAt(0).toUpperCase()}
                    </div>

                    <div className="desktop-user-info">

                        <strong>
                            {username}
                        </strong>

                        <span>
                            {role}
                        </span>

                    </div>

                </div>


                <button
                    type="button"
                    className="desktop-logout"
                    onClick={handleLogout}
                >

                    <LogOut
                        size={15}
                        strokeWidth={1.6}
                    />

                    <span>
                        Logout
                    </span>

                </button>

            </div>

        </aside>
    );
}

export default DesktopSidebar;