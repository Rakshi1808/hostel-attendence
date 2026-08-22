import { useState, useEffect, useRef } from "react";

// ── CONFIG ────────────────────────────────────────────────
const API_BASE = "http://localhost:8080"; // Spring Boot backend

// ── STYLES ───────────────────────────────────────────────
const css = `
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Space+Grotesk:wght@500;600;700&display=swap');

  .sd-root *{box-sizing:border-box;margin:0;padding:0}
  .sd-root{
    --navy:#0F1B2D;--navy2:#1A2D45;--navy3:#243B55;
    --teal:#00C9B1;--teal2:#00A896;
    --amber:#F59E0B;--green:#10B981;--red:#EF4444;
    --white:#F8FAFC;--muted:#94A3B8;--border:rgba(255,255,255,0.08);
    --card:rgba(26,45,69,0.8);
    background:var(--navy);font-family:'Inter',sans-serif;
    color:var(--white);min-height:100vh;
  }

  /* NAV */
  .sd-nav{display:flex;align-items:center;justify-content:space-between;
    padding:16px 24px;border-bottom:1px solid var(--border);
    backdrop-filter:blur(10px);position:sticky;top:0;z-index:100;
    background:rgba(15,27,45,0.9)}
  .sd-brand{font-family:'Space Grotesk',sans-serif;font-weight:700;
    font-size:16px;display:flex;align-items:center;gap:8px}
  .sd-brand-dot{width:8px;height:8px;border-radius:50%;background:var(--teal);
    box-shadow:0 0 8px var(--teal)}
  .sd-nav-right{display:flex;align-items:center;gap:12px}
  .sd-avatar{width:36px;height:36px;border-radius:50%;
    background:linear-gradient(135deg,var(--teal),var(--teal2));
    display:flex;align-items:center;justify-content:center;
    font-weight:600;font-size:13px;color:var(--navy);cursor:pointer}

  /* MAIN */
  .sd-main{max-width:900px;margin:0 auto;padding:24px 20px 80px}

  /* GREETING */
  .sd-greeting{margin-bottom:24px}
  .sd-greeting-sub{font-size:12px;color:var(--teal);font-weight:500;
    letter-spacing:0.08em;text-transform:uppercase;margin-bottom:6px}
  .sd-greeting-name{font-family:'Space Grotesk',sans-serif;font-size:26px;
    font-weight:700;line-height:1.2}
  .sd-greeting-name span{color:var(--teal)}
  .sd-greeting-meta{font-size:13px;color:var(--muted);margin-top:4px}

  /* STATS */
  .sd-stats{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin-bottom:24px}
  .sd-stat{background:var(--card);border:1px solid var(--border);
    border-radius:14px;padding:16px;backdrop-filter:blur(10px)}
  .sd-stat-label{font-size:11px;color:var(--muted);font-weight:500;
    letter-spacing:0.05em;text-transform:uppercase;margin-bottom:8px}
  .sd-stat-val{font-family:'Space Grotesk',sans-serif;font-size:28px;
    font-weight:700;line-height:1}
  .sd-stat-val.teal{color:var(--teal)}
  .sd-stat-val.green{color:var(--green)}
  .sd-stat-val.amber{color:var(--amber)}
  .sd-stat-sub{font-size:11px;color:var(--muted);margin-top:4px}
  .sd-stat-bar{height:3px;background:rgba(255,255,255,0.08);
    border-radius:2px;margin-top:10px;overflow:hidden}
  .sd-stat-bar-fill{height:3px;border-radius:2px;transition:width 0.8s ease}

  /* ATTENDANCE CARD */
  .sd-att-card{background:var(--card);border:1px solid var(--border);
    border-radius:18px;overflow:hidden;margin-bottom:20px;backdrop-filter:blur(10px)}
  .sd-att-header{padding:18px 20px 0;display:flex;align-items:center;
    justify-content:space-between}
  .sd-att-title{font-family:'Space Grotesk',sans-serif;font-size:16px;font-weight:600}
  .sd-window-badge{display:flex;align-items:center;gap:6px;
    background:rgba(0,201,177,0.1);border:1px solid rgba(0,201,177,0.2);
    border-radius:20px;padding:4px 10px;font-size:11px;color:var(--teal);font-weight:500}
  .sd-window-dot{width:6px;height:6px;border-radius:50%;background:var(--teal);
    animation:sd-pulse 1.5s infinite}
  @keyframes sd-pulse{0%,100%{opacity:1}50%{opacity:0.4}}

  /* CAMERA */
  .sd-camera{margin:16px 20px;border-radius:14px;overflow:hidden;
    position:relative;background:var(--navy3);aspect-ratio:4/3;
    display:flex;align-items:center;justify-content:center;
    border:1px solid var(--border);transition:border-color 0.3s}
  .sd-camera.success{background:rgba(16,185,129,0.05);
    border-color:rgba(16,185,129,0.3)}
  .sd-camera video{width:100%;height:100%;object-fit:cover;transform:scaleX(-1)}
  .sd-cam-placeholder{display:flex;flex-direction:column;align-items:center;
    gap:10px;color:var(--muted)}
  .sd-cam-placeholder-icon{font-size:36px}
  .sd-cam-placeholder-text{font-size:13px}
  .sd-corner{position:absolute;width:20px;height:20px;
    border-color:var(--teal);border-style:solid;opacity:0.8}
  .sd-corner.tl{top:10px;left:10px;border-width:2px 0 0 2px;border-radius:3px 0 0 0}
  .sd-corner.tr{top:10px;right:10px;border-width:2px 2px 0 0;border-radius:0 3px 0 0}
  .sd-corner.bl{bottom:10px;left:10px;border-width:0 0 2px 2px;border-radius:0 0 0 3px}
  .sd-corner.br{bottom:10px;right:10px;border-width:0 2px 2px 0;border-radius:0 0 3px 0}
  .sd-scan{position:absolute;left:10px;right:10px;height:2px;
    background:linear-gradient(90deg,transparent,var(--teal),transparent);
    animation:sd-scan 2s ease-in-out infinite;opacity:0.7}
  @keyframes sd-scan{0%{top:20px;opacity:0}20%{opacity:0.7}80%{opacity:0.7}
    100%{top:calc(100% - 20px);opacity:0}}
  .sd-cam-status{position:absolute;bottom:10px;left:50%;
    transform:translateX(-50%);background:rgba(15,27,45,0.85);
    backdrop-filter:blur(4px);border:1px solid var(--border);
    border-radius:20px;padding:5px 14px;font-size:11px;
    color:var(--muted);white-space:nowrap}

  /* SUCCESS OVERLAY */
  .sd-success{padding:20px 20px 24px;display:flex;flex-direction:column;
    align-items:center;gap:10px;text-align:center}
  .sd-success-icon{width:56px;height:56px;border-radius:50%;
    background:rgba(16,185,129,0.15);border:2px solid var(--green);
    display:flex;align-items:center;justify-content:center;font-size:24px}
  .sd-success-title{font-family:'Space Grotesk',sans-serif;font-size:18px;
    font-weight:600;color:var(--green)}
  .sd-success-sub{font-size:13px;color:var(--muted)}
  .sd-success-conf{font-size:12px;background:rgba(16,185,129,0.1);
    border:1px solid rgba(16,185,129,0.2);border-radius:20px;
    padding:4px 12px;color:var(--green);margin-top:2px}

  /* ERROR STATE */
  .sd-error-state{padding:16px 20px;background:rgba(239,68,68,0.08);
    border-top:1px solid rgba(239,68,68,0.15);display:flex;
    align-items:center;gap:10px}
  .sd-error-icon{font-size:18px}
  .sd-error-text{font-size:13px;color:var(--red);flex:1}
  .sd-error-retry{font-size:12px;color:var(--teal);cursor:pointer;
    font-weight:500;white-space:nowrap}

  /* ACTIONS */
  .sd-actions{padding:0 20px 20px;display:flex;gap:10px}
  .sd-btn-primary{flex:1;padding:14px;background:var(--teal);color:var(--navy);
    font-family:'Inter',sans-serif;font-weight:600;font-size:14px;
    border:none;border-radius:12px;cursor:pointer;transition:all 0.2s;
    display:flex;align-items:center;justify-content:center;gap:8px}
  .sd-btn-primary:hover:not(:disabled){background:var(--teal2);transform:translateY(-1px)}
  .sd-btn-primary:disabled{opacity:0.5;cursor:not-allowed}
  .sd-btn-secondary{padding:14px 18px;background:transparent;color:var(--muted);
    font-family:'Inter',sans-serif;font-weight:500;font-size:14px;
    border:1px solid var(--border);border-radius:12px;cursor:pointer;transition:all 0.2s}
  .sd-btn-secondary:hover{border-color:var(--teal);color:var(--teal)}

  /* SPINNER */
  .sd-spinner{width:18px;height:18px;border:2px solid rgba(15,27,45,0.3);
    border-top-color:var(--navy);border-radius:50%;
    animation:sd-spin 0.8s linear infinite}
  @keyframes sd-spin{to{transform:rotate(360deg)}}

  /* HISTORY */
  .sd-section-title{font-family:'Space Grotesk',sans-serif;font-size:15px;
    font-weight:600;margin-bottom:14px;display:flex;align-items:center;
    justify-content:space-between}
  .sd-section-link{font-size:12px;color:var(--teal);font-weight:500;cursor:pointer}
  .sd-history{background:var(--card);border:1px solid var(--border);
    border-radius:14px;overflow:hidden;backdrop-filter:blur(10px)}
  .sd-history-row{display:flex;align-items:center;gap:14px;
    padding:13px 16px;border-bottom:1px solid var(--border)}
  .sd-history-row:last-child{border-bottom:none}
  .sd-date-box{min-width:42px;text-align:center}
  .sd-date-day{font-size:18px;font-weight:600;font-family:'Space Grotesk',sans-serif;line-height:1}
  .sd-date-month{font-size:10px;color:var(--muted);text-transform:uppercase;
    letter-spacing:0.05em}
  .sd-hist-info{flex:1}
  .sd-hist-time{font-size:13px;font-weight:500}
  .sd-hist-meta{font-size:11px;color:var(--muted);margin-top:2px}
  .sd-conf-bar{height:2px;background:rgba(255,255,255,0.06);
    border-radius:1px;margin-top:4px;overflow:hidden}
  .sd-conf-fill{height:2px;border-radius:1px;background:var(--teal)}
  .sd-badge{font-size:11px;font-weight:500;padding:3px 10px;border-radius:20px}
  .sd-badge.present{background:rgba(16,185,129,0.15);color:var(--green);
    border:1px solid rgba(16,185,129,0.2)}
  .sd-badge.absent{background:rgba(239,68,68,0.12);color:var(--red);
    border:1px solid rgba(239,68,68,0.2)}

  /* LOADING SKELETON */
  .sd-skeleton{background:linear-gradient(90deg,var(--navy2) 25%,
    var(--navy3) 50%,var(--navy2) 75%);background-size:200% 100%;
    animation:sd-shimmer 1.5s infinite;border-radius:8px}
  @keyframes sd-shimmer{0%{background-position:200% 0}
    100%{background-position:-200% 0}}

  /* BOTTOM NAV */
  .sd-bottom-nav{position:fixed;bottom:0;left:0;right:0;
    background:rgba(15,27,45,0.95);backdrop-filter:blur(20px);
    border-top:1px solid var(--border);display:flex;
    padding:8px 0 12px;z-index:100}
  .sd-nav-item{flex:1;display:flex;flex-direction:column;align-items:center;
    gap:3px;cursor:pointer;padding:4px 0}
  .sd-nav-item-icon{font-size:20px;color:var(--muted);transition:color 0.2s}
  .sd-nav-item-label{font-size:10px;color:var(--muted);transition:color 0.2s}
  .sd-nav-item.active .sd-nav-item-icon,
  .sd-nav-item.active .sd-nav-item-label{color:var(--teal)}

  /* TOAST */
  .sd-toast{position:fixed;top:70px;left:50%;
    transform:translateX(-50%) translateY(-10px);
    background:var(--navy2);border:1px solid var(--border);
    border-radius:10px;padding:10px 18px;font-size:13px;
    color:var(--white);opacity:0;transition:all 0.3s;
    pointer-events:none;z-index:200;white-space:nowrap}
  .sd-toast.sd-toast-show{opacity:1;transform:translateX(-50%) translateY(0)}
  .sd-toast.sd-toast-error{border-color:rgba(239,68,68,0.3);
    background:rgba(239,68,68,0.15)}
  .sd-toast.sd-toast-success{border-color:rgba(16,185,129,0.3);
    background:rgba(16,185,129,0.15)}
`;

// ── ATTENDANCE STEP MACHINE ─────────────────────────────
// idle → camera → verifying → success | error
const STEPS = { IDLE: 0, CAMERA: 1, VERIFYING: 2, SUCCESS: 3, ERROR: 4 };

// ── HELPERS ───────────────────────────────────────────────
function getInitials(name = "") {
  return name.split(" ").map(w => w[0]).join("").slice(0, 2).toUpperCase();
}

function formatTime(ts) {
  return new Date(ts).toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" });
}

function formatDay(ts) {
  const d = new Date(ts);
  return { day: d.getDate(), month: d.toLocaleString("en-IN", { month: "short" }) };
}

// ── MAIN COMPONENT ────────────────────────────────────────
export default function StudentDashboard() {
  const [student, setStudent]     = useState(null);
  const [stats, setStats]         = useState(null);
  const [history, setHistory]     = useState([]);
  const [step, setStep]           = useState(STEPS.IDLE);
  const [result, setResult]       = useState(null);
  const [errorMsg, setErrorMsg]   = useState("");
  const [toast, setToast]         = useState({ msg: "", type: "", show: false });
  const [activeTab, setActiveTab] = useState("home");
  const [loading, setLoading]     = useState(true);

  const videoRef  = useRef(null);
  const streamRef = useRef(null);
  const token     = localStorage.getItem("token");

  // ── AUTH HEADERS ────────────────────────────────────────
  const headers = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };

  // ── LOAD STUDENT DATA ───────────────────────────────────
  useEffect(() => {
    if (!token) { window.location.href = "/login"; return; }
    loadData();
  }, []);

  async function loadData() {
    try {
      const [studentRes, statsRes, historyRes] = await Promise.all([
        fetch(`${API_BASE}/student/me`, { headers }),
        fetch(`${API_BASE}/attendance/stats`, { headers }),
        fetch(`${API_BASE}/attendance/history?limit=5`, { headers }),
      ]);
      if (studentRes.ok) setStudent(await studentRes.json());
      if (statsRes.ok)   setStats(await statsRes.json());
      if (historyRes.ok) setHistory(await historyRes.json());
    } catch (e) {
      showToast("Could not load data — check connection", "error");
    } finally {
      setLoading(false);
    }
  }

  // ── TOAST ────────────────────────────────────────────────
  function showToast(msg, type = "info") {
    setToast({ msg, type, show: true });
    setTimeout(() => setToast(t => ({ ...t, show: false })), 2500);
  }

  // ── CAMERA OPEN ─────────────────────────────────────────
  async function openCamera() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "user", width: 640, height: 480 },
      });
      streamRef.current = stream;
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
      }
      setStep(STEPS.CAMERA);
    } catch (e) {
      showToast("Camera access denied — allow camera in browser settings", "error");
    }
  }

  // ── STOP CAMERA ─────────────────────────────────────────
  function stopCamera() {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach(t => t.stop());
      streamRef.current = null;
    }
  }

  // ── CAPTURE + MARK ──────────────────────────────────────
  async function captureAndMark() {
    setStep(STEPS.VERIFYING);

    try {
      // capture frame from video → canvas → base64
      const video  = videoRef.current;
      const canvas = document.createElement("canvas");
      canvas.width  = video.videoWidth;
      canvas.height = video.videoHeight;
      canvas.getContext("2d").drawImage(video, 0, 0);
      const base64 = canvas.toDataURL("image/jpeg", 0.85).split(",")[1];

      stopCamera();

      // call Spring Boot /attendance
      const res = await fetch(`${API_BASE}/attendance`, {
        method: "POST",
        headers,
        body: JSON.stringify({
          roll_no:      student?.roll_no,
          image_base64: base64,
        }),
      });

      const data = await res.json();

      if (res.ok) {
        setResult(data);
        setStep(STEPS.SUCCESS);
        showToast("Attendance marked successfully", "success");
        loadData(); // refresh stats + history
      } else {
        // map backend error codes to user-friendly messages
        const msgs = {
          face_match_failed:    "Face not recognised — try again in better lighting",
          no_face_detected:     "No face detected — look directly at the camera",
          student_not_registered: "Face not registered — contact your warden",
          outside_time_window:  "Attendance window is closed (08:30–09:30 AM only)",
          already_marked:       "You've already marked attendance today",
          wifi_invalid:         "Connect to hostel WiFi first",
        };
        setErrorMsg(msgs[data.error] || "Verification failed — please try again");
        setStep(STEPS.ERROR);
      }
    } catch (e) {
      stopCamera();
      setErrorMsg("Network error — check your connection and try again");
      setStep(STEPS.ERROR);
    }
  }

  // ── RETRY ───────────────────────────────────────────────
  function retry() {
    setStep(STEPS.IDLE);
    setErrorMsg("");
    setResult(null);
  }

  // ── BUTTON LOGIC ────────────────────────────────────────
  function handleBtn() {
    if (step === STEPS.IDLE)    openCamera();
    if (step === STEPS.CAMERA)  captureAndMark();
  }

  const btnLabel = {
    [STEPS.IDLE]:      { icon: "📷", text: "Open Camera" },
    [STEPS.CAMERA]:    { icon: "✓",  text: "Capture & Mark Attendance" },
    [STEPS.VERIFYING]: { icon: null, text: "Verifying face…" },
  }[step] || {};

  // ── RENDER ───────────────────────────────────────────────
  return (
    <div className="sd-root">
      <style>{css}</style>

      {/* TOAST */}
      <div className={`sd-toast ${toast.show ? "sd-toast-show" : ""} ${
        toast.type === "error" ? "sd-toast-error" :
        toast.type === "success" ? "sd-toast-success" : ""
      }`}>{toast.msg}</div>

      {/* NAV */}
      <nav className="sd-nav">
        <div className="sd-brand">
          <div className="sd-brand-dot" />
          NIT KKR Hostel
        </div>
        <div className="sd-nav-right">
          <div className="sd-avatar">
            {loading ? "?" : getInitials(student?.name)}
          </div>
        </div>
      </nav>

      <div className="sd-main">

        {/* GREETING */}
        <div className="sd-greeting">
          <div className="sd-greeting-sub">Good morning</div>
          {loading ? (
            <>
              <div className="sd-skeleton" style={{ height: 32, width: 200, marginBottom: 8 }} />
              <div className="sd-skeleton" style={{ height: 16, width: 300 }} />
            </>
          ) : (
            <>
              <div className="sd-greeting-name">
                {student?.name?.split(" ")[0]} <span>{student?.name?.split(" ").slice(1).join(" ")}</span>
              </div>
              <div className="sd-greeting-meta">
                Roll No. {student?.roll_no} &nbsp;·&nbsp;
                {student?.hostel_name}, Room {student?.room_no} &nbsp;·&nbsp;
                {student?.programme} Year {student?.year_of_study}
              </div>
            </>
          )}
        </div>

        {/* STATS */}
        <div className="sd-stats">
          <div className="sd-stat">
            <div className="sd-stat-label">This Month</div>
            <div className="sd-stat-val teal">
              {loading ? "—" : `${stats?.percentage ?? 0}`}
              <span style={{ fontSize: 16, color: "var(--muted)" }}>%</span>
            </div>
            <div className="sd-stat-sub">Attendance rate</div>
            <div className="sd-stat-bar">
              <div className="sd-stat-bar-fill" style={{
                width: `${stats?.percentage ?? 0}%`,
                background: "var(--teal)"
              }} />
            </div>
          </div>
          <div className="sd-stat">
            <div className="sd-stat-label">Present</div>
            <div className="sd-stat-val green">{loading ? "—" : stats?.present ?? 0}</div>
            <div className="sd-stat-sub">Days this month</div>
            <div className="sd-stat-bar">
              <div className="sd-stat-bar-fill" style={{
                width: `${stats ? (stats.present / stats.total) * 100 : 0}%`,
                background: "var(--green)"
              }} />
            </div>
          </div>
          <div className="sd-stat">
            <div className="sd-stat-label">Absent</div>
            <div className="sd-stat-val amber">{loading ? "—" : stats?.absent ?? 0}</div>
            <div className="sd-stat-sub">Days this month</div>
            <div className="sd-stat-bar">
              <div className="sd-stat-bar-fill" style={{
                width: `${stats ? (stats.absent / stats.total) * 100 : 0}%`,
                background: "var(--amber)"
              }} />
            </div>
          </div>
        </div>

        {/* ATTENDANCE CARD */}
        <div className="sd-att-card">
          <div className="sd-att-header">
            <div className="sd-att-title">Mark Attendance</div>
            <div className="sd-window-badge">
              <div className="sd-window-dot" />
              08:30 – 09:30 AM
            </div>
          </div>

          {/* CAMERA AREA */}
          {step !== STEPS.SUCCESS && (
            <div className={`sd-camera ${step === STEPS.ERROR ? "success" : ""}`}
              style={step === STEPS.ERROR ? { background: "rgba(239,68,68,0.05)", borderColor: "rgba(239,68,68,0.3)" } : {}}>
              {step === STEPS.IDLE && (
                <div className="sd-cam-placeholder">
                  <div className="sd-cam-placeholder-icon">📷</div>
                  <div className="sd-cam-placeholder-text">Tap "Open Camera" to begin</div>
                </div>
              )}
              <video
                ref={videoRef}
                autoPlay
                playsInline
                style={{ display: step === STEPS.CAMERA || step === STEPS.VERIFYING ? "block" : "none" }}
              />
              <div className="sd-corner tl" />
              <div className="sd-corner tr" />
              <div className="sd-corner bl" />
              <div className="sd-corner br" />
              {(step === STEPS.CAMERA) && (
                <>
                  <div className="sd-scan" />
                  <div className="sd-cam-status">Face detected · Ready to mark</div>
                </>
              )}
              {step === STEPS.VERIFYING && (
                <div className="sd-cam-status">Verifying with AI…</div>
              )}
            </div>
          )}

          {/* SUCCESS STATE */}
          {step === STEPS.SUCCESS && (
            <div className="sd-success">
              <div className="sd-success-icon">✓</div>
              <div className="sd-success-title">Attendance Marked</div>
              <div className="sd-success-sub">
                Today, {result?.marked_at ? formatTime(result.marked_at) : "—"} · {student?.hostel_name}
              </div>
              <div className="sd-success-conf">
                Face match · {result?.confidence ? `${(result.confidence * 100).toFixed(1)}%` : "—"} confidence
              </div>
            </div>
          )}

          {/* ERROR STATE */}
          {step === STEPS.ERROR && (
            <div className="sd-error-state">
              <div className="sd-error-icon">⚠️</div>
              <div className="sd-error-text">{errorMsg}</div>
              <div className="sd-error-retry" onClick={retry}>Try again</div>
            </div>
          )}

          {/* ACTIONS */}
          {step !== STEPS.SUCCESS && (
            <div className="sd-actions">
              <button
                className="sd-btn-primary"
                onClick={handleBtn}
                disabled={step === STEPS.VERIFYING}
              >
                {step === STEPS.VERIFYING
                  ? <><div className="sd-spinner" />{btnLabel.text}</>
                  : <>{btnLabel.icon} {btnLabel.text}</>
                }
              </button>
              <button
                className="sd-btn-secondary"
                onClick={() => showToast("Scroll down to see your history")}
              >
                History
              </button>
            </div>
          )}
        </div>

        {/* HISTORY */}
        <div className="sd-section-title">
          Attendance History
          <span className="sd-section-link" onClick={() => showToast("Full history coming soon")}>
            View all →
          </span>
        </div>

        <div className="sd-history">
          {loading ? (
            [1,2,3].map(i => (
              <div key={i} className="sd-history-row">
                <div className="sd-skeleton" style={{ width: 42, height: 44 }} />
                <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 6 }}>
                  <div className="sd-skeleton" style={{ height: 14, width: "60%" }} />
                  <div className="sd-skeleton" style={{ height: 11, width: "40%" }} />
                </div>
                <div className="sd-skeleton" style={{ width: 60, height: 22, borderRadius: 20 }} />
              </div>
            ))
          ) : history.length === 0 ? (
            <div style={{ padding: "24px", textAlign: "center", color: "var(--muted)", fontSize: 13 }}>
              No attendance history yet
            </div>
          ) : (
            history.map((h, i) => {
              const { day, month } = formatDay(h.marked_at);
              return (
                <div key={i} className="sd-history-row">
                  <div className="sd-date-box">
                    <div className="sd-date-day">{day}</div>
                    <div className="sd-date-month">{month}</div>
                  </div>
                  <div className="sd-hist-info">
                    <div className="sd-hist-time">
                      {h.status === "present" ? `Marked at ${formatTime(h.marked_at)}` : "Window missed"}
                    </div>
                    <div className="sd-hist-meta">
                      {student?.hostel_name} · {h.status === "present" ? "WiFi verified" : "No mark between 08:30–09:30"}
                    </div>
                    <div className="sd-conf-bar">
                      <div className="sd-conf-fill" style={{ width: `${(h.confidence ?? 0) * 100}%` }} />
                    </div>
                  </div>
                  <span className={`sd-badge ${h.status}`}>
                    {h.status === "present" ? "Present" : "Absent"}
                  </span>
                </div>
              );
            })
          )}
        </div>

      </div>

      {/* BOTTOM NAV */}
      <div className="sd-bottom-nav">
        {[
          { id: "home",       icon: "🏠", label: "Home" },
          { id: "attendance", icon: "📋", label: "Attendance" },
          { id: "notices",    icon: "📢", label: "Notices" },
          { id: "profile",    icon: "👤", label: "Profile" },
        ].map(tab => (
          <div
            key={tab.id}
            className={`sd-nav-item ${activeTab === tab.id ? "active" : ""}`}
            onClick={() => { setActiveTab(tab.id); showToast(`${tab.label} tab`); }}
          >
            <div className="sd-nav-item-icon">{tab.icon}</div>
            <div className="sd-nav-item-label">{tab.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
