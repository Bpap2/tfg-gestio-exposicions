import { Routes, Route, Navigate, Outlet } from "react-router-dom";
import RequireAuth from "./auth/RequireAuth";

import Login from "./pages/Login";
import Exhibitions from "./pages/Exhibitions";
import ExhibitionDetail from "./pages/ExhibitionDetail";
import Lenders from "./pages/Lenders";
import LenderDetail from "./pages/LenderDetail"; // (millor sense .jsx)

function PrivateRoutes() {
    return (
        <RequireAuth>
            <Outlet />
        </RequireAuth>
    );
}

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />

            <Route element={<PrivateRoutes />}>
                <Route path="/" element={<Navigate to="/exhibitions" replace />} />

                <Route path="/exhibitions" element={<Exhibitions />} />
                <Route path="/exhibitions/:id" element={<ExhibitionDetail />} />

                <Route path="/lenders" element={<Lenders />} />
                <Route path="/lenders/:id" element={<LenderDetail />} />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}
