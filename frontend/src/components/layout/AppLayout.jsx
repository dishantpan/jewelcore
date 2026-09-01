import { Outlet } from "react-router-dom";
import DesktopSidebar from "./DesktopSidebar";
import MobileNav from "./MobileNav";
import "./AppLayout.css";

function AppLayout() {
    return (
        <div className="app-shell">

            {/* Desktop / tablet navigation */}
            <DesktopSidebar />

            {/* Main application area */}
            <main className="app-main">
                <Outlet />
            </main>

            {/* Mobile bottom navigation */}
            <MobileNav />

        </div>
    );
}

export default AppLayout;