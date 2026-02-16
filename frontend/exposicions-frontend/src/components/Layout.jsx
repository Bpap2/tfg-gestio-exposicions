import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function Layout({ title, children }) {
    const { user, logout } = useAuth();
    const { pathname } = useLocation();

    return (
        <>
            <header className="topbar">
                <div className="brand">CCCB · Gestió d’exposicions</div>
                <nav className="nav">
                    <Link className={`navlink ${pathname.startsWith("/exhibitions") ? "active" : ""}`} to="/exhibitions">
                        Exposicions
                    </Link>
                    {user && (
                        <button className="btn" onClick={logout}>
                            Logout
                        </button>
                    )}
                </nav>
            </header>

            <main className="container">
                <h1 className="h1">{title}</h1>
                {children}
            </main>
        </>
    );
}
