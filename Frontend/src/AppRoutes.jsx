import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";

import Register from "./pages/Register";
import SetupMfa from "./pages/SetupMfa";
import ActivateMfa from "./pages/ActivateMfa";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={<Register />}
        />

        <Route
          path="/setup-mfa"
          element={<SetupMfa />}
        />

        <Route
          path="/activate-mfa"
          element={<ActivateMfa />}
        />

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/dashboard"
          element={<Dashboard />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;