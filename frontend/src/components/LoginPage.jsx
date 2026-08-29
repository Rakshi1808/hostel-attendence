import { useState } from "react";

const API_BASE = "http://localhost:8080";

const css = `
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Space+Grotesk:wght@500;600;700&display=swap');

  .login-root *{box-sizing:border-box;margin:0;padding:0}
  .login-root{
    --navy:#0F1B2D;--navy2:#1A2D45;--navy3:#243B55;
    --teal:#00C9B1;--teal2:#00A896;
    --red:#EF4444;--white:#F8FAFC;--muted:#94A3B8;
    --border:rgba(255,255,255,0.08);
    min-height:100vh;background:var(--navy);
    font-family:'Inter',sans-serif;color:var(--white);
    display:flex;flex-direction:column;align-items:center;
    justify-content:center;padding:24px;
  }
  .login-logo{display:flex;align-items:center;gap:10px;margin-bottom:32px}
  .login-logo-dot{width:10px;height:10px;border-radius:50%;
    background:var(--teal);box-shadow:0 0 12px var(--teal)}
  .login-logo-text{font-family:'Space Grotesk',sans-serif;font-size:20px;font-weight:700}
  .login-card{width:100%;max-width:400px;background:rgba(26,45,69,0.8);
    border:1px solid var(--border);border-radius:20px;padding:32px;
    backdrop-filter:blur(10px)}
  .login-title{font-family:'Space Grotesk',sans-serif;font-size:24px;
    font-weight:700;margin-bottom:6px}
  .login-sub{font-size:13px;color:var(--muted);margin-bottom:28px}
  .login-tabs{display:flex;background:var(--navy3);border-radius:10px;
    padding:4px;margin-bottom:24px;gap:4px}
  .login-tab{flex:1;padding:8px;text-align:center;border-radius:7px;
    font-size:13px;font-weight:500;cursor:pointer;color:var(--muted);
    transition:all 0.2s;border:none;background:transparent}
  .login-tab.active{background:var(--navy2);color:var(--white);
    box-shadow:0 1px 4px rgba(0,0,0,0.3)}
  .login-field{margin-bottom:16px}
  .login-label{font-size:12px;font-weight:500;color:var(--muted);
    letter-spacing:0.04em;text-transform:uppercase;margin-bottom:7px;display:block}
  .login-input-wrap{position:relative}
  .login-input{width:100%;padding:13px 14px;background:var(--navy3);
    border:1px solid var(--border);border-radius:10px;color:var(--white);
    font-family:'Inter',sans-serif;font-size:14px;outline:none;transition:border-color 0.2s}
  .login-input::placeholder{color:var(--muted)}
  .login-input:focus{border-color:var(--teal)}
  .login-input.error{border-color:var(--red)}
  .login-input-icon{position:absolute;right:14px;top:50%;
    transform:translateY(-50%);font-size:16px;cursor:pointer;color:var(--muted)}
  .login-hint{background:rgba(0,201,177,0.06);border:1px solid rgba(0,201,177,0.15);
    border-radius:8px;padding:10px 12px;font-size:12px;color:var(--muted);
    margin-bottom:20px;line-height:1.5}
  .login-hint span{color:var(--teal);font-weight:500}
  .login-btn{width:100%;padding:14px;background:var(--teal);color:var(--navy);
    font-family:'Inter',sans-serif;font-weight:600;font-size:15px;
    border:none;border-radius:12px;cursor:pointer;transition:all 0.2s;
    display:flex;align-items:center;justify-content:center;gap:8px;margin-top:4px}
  .login-btn:hover:not(:disabled){background:var(--teal2);transform:translateY(-1px)}
  .login-btn:disabled{opacity:0.5;cursor:not-allowed;transform:none}
  .login-spinner{width:18px;height:18px;border:2px solid rgba(15,27,45,0.3);
    border-top-color:var(--navy);border-radius:50%;animation:login-spin 0.8s linear infinite}
  @keyframes login-spin{to{transform:rotate(360deg)}}
  .login-banner{background:rgba(239,68,68,0.1);border:1px solid rgba(239,68,68,0.2);
    border-radius:10px;padding:12px 14px;font-size:13px;color:var(--red);
    margin-bottom:16px;display:flex;align-items:center;gap:8px}
  .login-footer{margin-top:24px;text-align:center;font-size:12px;color:var(--muted)}
  .login-footer span{color:var(--teal);cursor:pointer;font-weight:500}
  .login-test-btn{width:100%;padding:10px;margin-top:8px;background:transparent;
    border:1px dashed #94A3B8;border-radius:12px;color:#94A3B8;cursor:pointer;
    font-size:12px;font-family:'Inter',sans-serif;transition:all 0.2s}
  .login-test-btn:hover{border-color:var(--teal);color:var(--teal)}
`;

export default function LoginPage({ onLogin }) {
  const [role,     setRole]     = useState("student");
  const [rollNo,   setRollNo]   = useState("");
  const [email,    setEmail]    = useState("");
  const [password, setPassword] = useState("");
  const [showPw,   setShowPw]   = useState(false);
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState("");

  const derivedEmail = rollNo ? `${rollNo}@nitkkr.ac.in` : "";

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    if (role === "student" && !rollNo.trim()) {
      setError("Enter your roll number"); return;
    }
    if ((role === "warden" || role === "admin") && !email.trim()) {
      setError("Enter your email"); return;
    }
    if (!password.trim()) {
      setError("Enter your password"); return;
    }

    setLoading(true);
    try {
      const body = role === "student"
        ? { roll_no: rollNo.trim(), password }
        : { email: email.trim(), password };

      const endpoint = role === "admin"
        ? `${API_BASE}/auth/admin/login`
        : role === "warden"
        ? `${API_BASE}/auth/warden/login`
        : `${API_BASE}/auth/student/login`;

      const res  = await fetch(endpoint, {
        method:  "POST",
        headers: { "Content-Type": "application/json" },
        body:    JSON.stringify(body),
      });
      const data = await res.json();

      if (res.ok) {
        localStorage.setItem("token",   data.token);
        localStorage.setItem("role",    role);
        if (data.roll_no) localStorage.setItem("roll_no", data.roll_no);
        onLogin(role);
      } else {
        const msgs = {
          invalid_credentials: "Incorrect roll number or password",
          account_suspended:   "Your account is suspended — contact admin",
          face_pending:        "Complete face registration before logging in",
          account_pending:     "Account not activated yet — check your email",
        };
        setError(msgs[data.error] || "Login failed — please try again");
      }
    } catch (e) {
      setError("Network error — check your connection");
    } finally {
      setLoading(false);
    }
  }

  // ── TEST BYPASS — remove before final demo ─────────────
  function handleTestLogin(testRole) {
    localStorage.setItem("token",   "dummy-jwt-token");
    localStorage.setItem("role",    testRole);
    localStorage.setItem("roll_no", "525410037");
    onLogin(testRole);
  }

  return (
    <div className="login-root">
      <style>{css}</style>

      <div className="login-logo">
        <div className="login-logo-dot" />
        <div className="login-logo-text">NIT KKR Hostel</div>
      </div>

      <div className="login-card">
        <div className="login-title">Welcome back</div>
        <div className="login-sub">Sign in to mark your attendance</div>

        {/* ROLE TABS */}
        <div className="login-tabs">
          {["student", "warden", "admin"].map(r => (
            <button
              key={r}
              className={`login-tab ${role === r ? "active" : ""}`}
              onClick={() => { setRole(r); setError(""); }}
            >
              {r.charAt(0).toUpperCase() + r.slice(1)}
            </button>
          ))}
        </div>

        {/* ERROR BANNER */}
        {error && (
          <div className="login-banner">
            <span>⚠️</span> {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>

          {/* STUDENT: roll number */}
          {role === "student" && (
            <>
              <div className="login-field">
                <label className="login-label">Roll Number</label>
                <input
                  className={`login-input ${error && !rollNo ? "error" : ""}`}
                  placeholder="e.g. 525410037"
                  value={rollNo}
                  onChange={e => { setRollNo(e.target.value); setError(""); }}
                  autoFocus
                />
              </div>
              {rollNo && (
                <div className="login-hint">
                  Your email: <span>{derivedEmail}</span>
                </div>
              )}
            </>
          )}

          {/* WARDEN / ADMIN: email */}
          {(role === "warden" || role === "admin") && (
            <div className="login-field">
              <label className="login-label">Email</label>
              <input
                className={`login-input ${error && !email ? "error" : ""}`}
                placeholder="yourname@nitkkr.ac.in"
                type="email"
                value={email}
                onChange={e => { setEmail(e.target.value); setError(""); }}
                autoFocus
              />
            </div>
          )}

          {/* PASSWORD */}
          <div className="login-field">
            <label className="login-label">Password</label>
            <div className="login-input-wrap">
              <input
                className={`login-input ${error && !password ? "error" : ""}`}
                placeholder="Enter your password"
                type={showPw ? "text" : "password"}
                value={password}
                onChange={e => { setPassword(e.target.value); setError(""); }}
                style={{ paddingRight: 44 }}
              />
              <span className="login-input-icon" onClick={() => setShowPw(s => !s)}>
                {showPw ? "🙈" : "👁️"}
              </span>
            </div>
          </div>

          {/* REAL LOGIN BUTTON */}
          <button className="login-btn" type="submit" disabled={loading}>
            {loading
              ? <><div className="login-spinner" /> Signing in…</>
              : `Sign in as ${role.charAt(0).toUpperCase() + role.slice(1)}`
            }
          </button>

          {/* ── TEST BUTTONS — remove before final demo ── */}
          <button
            type="button"
            className="login-test-btn"
            onClick={() => handleTestLogin("student")}
          >
            🧪 Test Student Dashboard
          </button>
          <button
            type="button"
            className="login-test-btn"
            onClick={() => handleTestLogin("warden")}
          >
            🧪 Test Warden Dashboard
          </button>
          <button
            type="button"
            className="login-test-btn"
            onClick={() => handleTestLogin("admin")}
          >
            🧪 Test Admin Dashboard
          </button>

        </form>

        <div className="login-footer">
          Forgot password? <span onClick={() => alert("Contact your hostel warden")}>Contact warden</span>
        </div>
      </div>
    </div>
  );
}