import {
    LayoutDashboard,
    Gem,
    Boxes,
    Calculator,
    Receipt,
    Menu,
} from "lucide-react";

import { useLocation, useNavigate } from "react-router-dom";

function MobileNav() {

    const navigate = useNavigate();
    const location = useLocation();

    const navigation = [
        {
            label: "Home",
            path: "/dashboard",
            icon: LayoutDashboard,
        },
        {
            label: "Jewellery",
            path: "/jewellery",
            icon: Gem,
        },
        {
            label: "Stock",
            path: "/inventory",
            icon: Boxes,
        },
        {
            label: "Price",
            path: "/pricing",
            icon: Calculator,
        },
        {
            label: "Billing",
            path: "/billing",
            icon: Receipt,
        },
    ];

    const isActive = (path) => {

        if (path === "/dashboard") {
            return location.pathname === "/dashboard";
        }

        return location.pathname.startsWith(path);
    };

    return (
        <nav className="mobile-nav">

            {navigation.map((item) => {

                const Icon = item.icon;

                return (
                    <button
                        key={item.path}
                        type="button"
                        className={`mobile-nav-item ${
                            isActive(item.path)
                                ? "active"
                                : ""
                        }`}
                        onClick={() =>
                            navigate(item.path)
                        }
                    >

                        <Icon
                            size={20}
                            strokeWidth={1.7}
                        />

                        <span>
                            {item.label}
                        </span>

                    </button>
                );
            })}


            <button
                type="button"
                className="mobile-nav-item"
                onClick={() => navigate("/menu")}
            >

                <Menu
                    size={20}
                    strokeWidth={1.7}
                />

                <span>
                    More
                </span>

            </button>

        </nav>
    );
}

export default MobileNav;