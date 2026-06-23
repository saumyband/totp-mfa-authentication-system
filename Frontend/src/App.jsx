import { BrowserRouter, Route, Routes } from "react-router-dom";
import { ActivateMfa } from "./pages/ActivateMfa";
import { Dashboard } from "./pages/Dashboard";
import { Login } from "./pages/Login";
import { Register } from "./pages/Register";
import { SetupMfa } from "./pages/SetupMfa";

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/setup-mfa" element={<SetupMfa />} />
          <Route path="/activate-mfa" element={<ActivateMfa />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;