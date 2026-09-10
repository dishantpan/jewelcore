import {
    BrowserRouter,
    Navigate,
    Route,
    Routes,
} from "react-router-dom";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Jewellery from "./pages/Jewellery";
import JewelleryDetails from "./pages/JewelleryDetails";
import Inventory from "./pages/Inventory";
import InventoryDetails from "./pages/InventoryDetails";
import Pricing from "./pages/Pricing";
import Billing from "./pages/Billing";
import AppLayout from "./components/layout/AppLayout";
import MetalPrices from "./pages/MetalPrices";
import Categories from "./pages/Categories";
import Metals from "./pages/Metals";
import Purities from "./pages/Purities";

import "./App.css";


function ProtectedRoute({ children }) {

    const token = localStorage.getItem("token");

    if (!token) {
        return <Navigate to="/login" replace />;
    }

    return children;
}


function PlaceholderPage({ title }) {

    return (
        <div className="placeholder-page">

            <div className="placeholder-eyebrow">
                JewelCore
            </div>

            <h1>
                {title}
            </h1>

            <p>
                This module is being prepared.
            </p>

        </div>
    );
}


function App() {

    return (
        <BrowserRouter>

            <Routes>

                {/* PUBLIC */}

                <Route
                    path="/login"
                    element={<Login />}
                />


                {/* PROTECTED APPLICATION */}

                <Route
                    element={
                        <ProtectedRoute>
                            <AppLayout />
                        </ProtectedRoute>
                    }
                >

                    {/* Dashboard */}

                    <Route
                        path="/dashboard"
                        element={<Dashboard />}
                    />


                    {/* Jewellery */}

                    <Route
                        path="/jewellery"
                        element={<Jewellery />}
                    />

                    <Route
                        path="/jewellery/:id"
                        element={<JewelleryDetails />}
                    />


                    {/* Inventory */}

                    <Route
                        path="/inventory"
                        element={<Inventory />}
                    />

                    <Route
                        path="/inventory/:id"
                        element={<InventoryDetails />}
                    />


                    {/* Pricing */}

                    <Route
                        path="/pricing"
                        element={<Pricing />}
                    />


                    {/* Billing */}

                    <Route
                        path="/billing"
                        element={<Billing />}
                    />


                    {/* Future modules */}

                    <Route
                        path="/categories"
                        element={<Categories />}
                    />

                    <Route
                        path="/metals"
                        element={<Metals />}
                    />

                    <Route
                        path="/purities"
                        element={<Purities />}
                    />

                    <Route
                        path="/metal-prices"
                        element={<MetalPrices />}
                    />

                    <Route
                        path="/reports"
                        element={
                            <PlaceholderPage title="Reports" />
                        }
                    />

                    <Route
                        path="/users"
                        element={
                            <PlaceholderPage title="Users" />
                        }
                    />

                    <Route
                        path="/menu"
                        element={
                            <PlaceholderPage title="Menu" />
                        }
                    />

                </Route>


                {/* DEFAULT */}

                <Route
                    path="/"
                    element={
                        <Navigate
                            to="/dashboard"
                            replace
                        />
                    }
                />

                <Route
                    path="*"
                    element={
                        <Navigate
                            to="/dashboard"
                            replace
                        />
                    }
                />

            </Routes>

        </BrowserRouter>
    );
}

export default App;