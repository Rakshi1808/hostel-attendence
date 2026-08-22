import { useState, useEffect } from "react";

const API_BASE = "http://localhost:8080";

const css = `
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Space+Grotesk:wght@500;600;700&display=swap');

  .wd-root *{box-sizing:border-box;margin:0;padding:0}
  .wd-root{
    --navy:#0F1B2D;--navy2:#1A2D45;--navy3:#243B55;
    --teal:#00C9B1;--teal2:#00A896;
    --amber:#F59E0B;--green:#10B981;--red:#EF4444;
    --white:#F8FAFC;--muted:#94A3B8;--border:rgba(255,255,255,0.08);
    --card:rgba(26,45,69,0.8);
    background:var(--navy);font-family:'Inter',sans-serif;
    color:var(--white);min-height:100vh;
  }

  /* NAV */
  .wd-nav{display:flex;align-items:center;justify-content:space-between;
    padding:16px 24px;border-bottom:1px solid var(--border);
    backdrop-filter:blur(10px);position:sticky;top:0;z-index:100;
    background:rgba(15,27,45,0.9)}
  .wd-brand{font-family:'Space Grotesk',sans-serif;font-weight:700;
    font-size:16px;display:flex;align-items:center;gap:8px}
  .wd-brand-dot{width:8px;height:8px;border-radius:50%;
    background:var(--teal);box-shadow:0 0 8px var(--teal)}
  .wd-role-badge{font-size:11px;font-weight:500;padding:3px 10px;
    border-radius:20px;background:rgba(0,201,177,0.1);
    border:1px solid rgba(0,201,177,0.2);color:var(--teal)}
  .wd-nav-right{display:flex;align-items:center;gap:10px}
  .wd-refresh-btn{padding:7px 14px;background:transparent;
    border:1px solid var(--border);border-radius:8px;color:var(--muted);
    font-size:12px;cursor:pointer;transition:all 0.2s;font-family:'Inter',sans-serif}
  .wd-refresh-btn:hover{border-color:var(--teal);color:var(--teal)}
  .wd-logout-btn{padding:7px 14px;background:transparent;
    border:1px solid var(--border);border-radius:8px;color:var(--muted);
    font-size:12px;cursor:pointer;font-family:'Inter',sans-serif}

  /* MAIN */
  .wd-main{max-width:1100px;margin:0 auto;padding:24px 20px 40px}

  /* HEADER ROW */
  .wd-header{margin-bottom:24px}
  .wd-header-sub{font-size:12px;color:var(--teal);font-weight:500;
    letter-spacing:0.08em;text-transform:uppercase;margin-bottom:6px}
  .wd-header-title{font-family:'Space Grotesk',sans-serif;font-size:24px;font-weight:700}
  .wd-header-meta{font-size:13px;color:var(--muted);margin-top:4px;
    display:flex;align-items:center;gap:8px}
  .wd-live-dot{width:6px;height:6px;border-radius:50%;
    background:var(--green);animation:wd-pulse 1.5s infinite}
  @keyframes wd-pulse{0%,100%{opacity:1}50%{opacity:0.3}}

  /* STATS */
  .wd-stats{display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin-bottom:24px}
  @media(max-width:700px){.wd-stats{grid-template-columns:repeat(2,1fr)}}
  .wd-stat{background:var(--card);border:1px solid var(--border);
    border-radius:14px;padding:16px;backdrop-filter:blur(10px)}
  .wd-stat-label{font-size:11px;color:var(--muted);font-weight:500;
    letter-spacing:0.05em;text-transform:uppercase;margin-bottom:8px}
  .wd-stat-val{font-family:'Space Grotesk',sans-serif;font-size:32px;font-weight:700;line-height:1}
  .wd-stat-val.teal{color:var(--teal)}
  .wd-stat-val.green{color:var(--green)}
  .wd-stat-val.amber{color:var(--amber)}
  .wd-stat-val.red{color:var(--red)}
  .wd-stat-sub{font-size:11px;color:var(--muted);margin-top:5px}
  .wd-stat-bar{height:3px;background:rgba(255,255,255,0.08);
    border-radius:2px;margin-top:10px;overflow:hidden}
  .wd-stat-bar-fill{height:3px;border-radius:2px;transition:width 0.8s ease}

  /* FILTER ROW */
  .wd-filter-row{display:flex;align-items:center;gap:10px;margin-bottom:16px;flex-wrap:wrap}
  .wd-search{flex:1;min-width:200px;padding:10px 14px;
    background:var(--navy2);border:1px solid var(--border);
    border-radius:10px;color:var(--white);font-family:'Inter',sans-serif;
    font-size:13px;outline:none;transition:border-color 0.2s}
  .wd-search::placeholder{color:var(--muted)}
  .wd-search:focus{border-color:var(--teal)}
  .wd-filter-btn{padding:10px 16px;border-radius:10px;font-size:12px;
    font-weight:500;cursor:pointer;border:1px solid var(--border);
    background:transparent;color:var(--muted);font-family:'Inter',sans-serif;
    transition:all 0.2s;white-space:nowrap}
  .wd-filter-btn.active{background:var(--teal);color:var(--navy);border-color:var(--teal)}
  .wd-filter-btn:hover:not(.active){border-color:var(--teal);color:var(--teal)}

  /* TABLE */
  .wd-table-wrap{background:var(--card);border:1px solid var(--border);
    border-radius:16px;overflow:hidden;backdrop-filter:blur(10px)}
  .wd-table{width:100%;border-collapse:collapse}
  .wd-table th{padding:12px 16px;text-align:left;font-size:11px;
    font-weight:500;color:var(--muted);letter-spacing:0.05em;
    text-transform:uppercase;border-bottom:1px solid var(--border);
    background:rgba(15,27,45,0.5)}
  .wd-table td{padding:13px 16px;font-size:13px;
    border-bottom:1px solid var(--border)}
  .wd-table tr:last-child td{border-bottom:none}
  .wd-table tr:hover td{background:rgba(255,255,255,0.02)}
  .wd-roll{font-family:'Space Grotesk',sans-serif;font-size:12px;
    color:var(--teal);font-weight:500}
  .wd-name{font-weight:500;color:var(--white)}
  .wd-room{font-size:12px;color:var(--muted);margin-top:2px}
  .wd-badge{font-size:11px;font-weight:500;padding:3px 10px;border-radius:20px}
  .wd-badge.present{background:rgba(16,185,129,0.15);color:var(--green);
    border:1px solid rgba(16,185,129,0.2)}
  .wd-badge.absent{background:rgba(239,68,68,0.12);color:var(--red);
    border:1px solid rgba(239,68,68,0.2)}
  .wd-badge.pending{background:rgba(245,158,11,0.12);color:var(--amber);
    border:1px solid rgba(245,158,11,0.2)}
  .wd-time{font-size:12px;color:var(--muted)}
  .wd-conf{display:flex;align-items:center;gap:6px}
  .wd-conf-bar{flex:1;height:3px;background:rgba(255,255,255,0.08);
    border-radius:2px;overflow:hidden;min-width:40px}
  .wd-conf-fill{height:3px;border-radius:2px;background:var(--teal)}
  .wd-conf-pct{font-size:11px;color:var(--muted);min-width:34px;text-align:right}
  .wd-override-btn{padding:5px 12px;background:transparent;
    border:1px solid var(--border);border-radius:6px;color:var(--muted);
    font-size:11px;cursor:pointer;font-family:'Inter',sans-serif;transition:all 0.2s}
  .wd-override-btn:hover{border-color:var(--teal);color:var(--teal)}

  /* EMPTY STATE */
  .wd-empty{padding:40px;text-align:center;color:var(--muted);font-size:13px}
  .wd-empty-icon{font-size:32px;margin-bottom:10px}

  /* SKELETON */
  .wd-skeleton{background:linear-gradient(90deg,var(--navy2) 25%,
    var(--navy3) 50%,var(--navy2) 75%);background-size:200% 100%;
    animation:wd-shimmer 1.5s infinite;border-radius:6px}
  @keyframes wd-shimmer{0%{background-position:200% 0}
    100%{background-position:-200% 0}}

  /* OVERRIDE MODAL */
  .wd-modal-bg{position:fixed;inset:0;background:rgba(0,0,0,0.6);
    backdrop-filter:blur(4px);z-index:200;display:flex;
    align-items:center;justify-content:center;padding:24px}
  .wd-modal{background:var(--navy2);border:1px solid var(--border);
    border-radius:16px;padding:24px;width:100%;max-width:380px}
  .wd-modal-title{font-family:'Space Grotesk',sans-serif;font-size:17px;
    font-weight:600;margin-bottom:6px}
  .wd-modal-sub{font-size:13px;color:var(--muted);margin-bottom:20px}
  .wd-modal-label{font-size:12px;font-weight:500;color:var(--muted);
    text-transform:uppercase;letter-spacing:0.04em;margin-bottom:7px;display:block}
  .wd-modal-textarea{width:100%;padding:12px;background:var(--navy3);
    border:1px solid var(--border);border-radius:10px;color:var(--white);
    font-family:'Inter',sans-serif;font-size:13px;outline:none;
    resize:none;transition:border-color 0.2s}
  .wd-modal-textarea:focus{border-color:var(--teal)}
  .wd-modal-actions{display:flex;gap:10px;margin-top:20px}
  .wd-modal-confirm{flex:1;padding:12px;background:var(--teal);color:var(--navy);
    font-weight:600;font-size:13px;border:none;border-radius:10px;cursor:pointer}
  .wd-modal-cancel{flex:1;padding:12px;background:transparent;color:var(--muted);
    font-size:13px;border:1px solid var(--border);border-radius:10px;cursor:pointer}
`;

// ── DUMMY DATA ─────────────────────────────────────────────
// remove when Spring Boot is ready
const DUMMY = [
  { roll_no:"525410037", name:"Arjun Kumar",  room:"H5-214", status:"present", marked_at:"2026-07-06T08:47:00", confidence:0.94 },
  { roll_no:"525410038", name:"Priya Sharma", room:"H1-312", status:"present", marked_at:"2026-07-06T09:02:00", confidence:0.89 },
  { roll_no:"525410039", name:"Ravi Patel",   room:"H5-108", status:"absent",  marked_at:null,                  confidence:0 },
  { roll_no:"525410040", name:"Sneha Reddy",  room:"H2-201", status:"present", marked_at:"2026-07-06T08:55:00", confidence:0.97 },
  { roll_no:"525410041", name:"Karan Singh",  room:"H3-401", status:"absent",  marked_at:null,                  confidence:0 },
  { roll_no:"525410042", name:"Divya Nair",   room:"H1-102", status:"present", marked_at:"2026-07-06T09:18:00", confidence:0.91 },
  { roll_no:"525410043", name:"Rohit Verma",  room:"H5-305", status:"pending", marked_at:null,                  confidence:0 },
];

function formatTime(ts) {
  if (!ts) return "—";
  return new Date(ts).toLocaleTimeString("en-IN", { hour:"2-digit", minute:"2-digit" });
}

export default function WardenDashboard({ onLogout }) {
  const [students, setStudents] = useState([]);
  const [loading, setLoading]   = useState(true);
  const [filter, setFilter]     = useState("all");   // all | present | absent
  const [search, setSearch]     = useState("");
  const [override, setOverride] = useState(null);    // student being overridden
  const [reason, setReason]     = useState("");
  const [lastRefresh, setLastRefresh] = useState(new Date());

  const token   = localStorage.getItem("token");
  const headers = { "Content-Type":"application/json", Authorization:`Bearer ${token}` };

  useEffect(() => { loadData(); }, []);

  // auto-refresh every 60 seconds
  useEffect(() => {
    const t = setInterval(() => { loadData(); }, 60000);
    return () => clearInterval(t);
  }, []);

  async function loadData() {
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/warden/attendance/today`, { headers });
      if (res.ok) {
        setStudents(await res.json());
      } else {
        // fallback to dummy data while backend is building
        setStudents(DUMMY);
      }
    } catch {
      setStudents(DUMMY);
    } finally {
      setLoading(false);
      setLastRefresh(new Date());
    }
  }

  async function submitOverride() {
    if (!reason.trim()) return;
    try {
      await fetch(`${API_BASE}/warden/attendance/override`, {
        method: "POST",
        headers,
        body: JSON.stringify({ roll_no: override.roll_no, reason }),
      });
      setStudents(s => s.map(st =>
        st.roll_no === override.roll_no
          ? { ...st, status:"present", marked_at: new Date().toISOString() }
          : st
      ));
    } catch {}
    setOverride(null);
    setReason("");
  }

  // filtered + searched list
  const displayed = students.filter(s => {
    const matchFilter = filter === "all" || s.status === filter;
    const matchSearch = s.name.toLowerCase().includes(search.toLowerCase()) ||
                        s.roll_no.includes(search) ||
                        s.room.toLowerCase().includes(search.toLowerCase());
    return matchFilter && matchSearch;
  });

  const total   = students.length;
  const present = students.filter(s => s.status === "present").length;
  const absent  = students.filter(s => s.status === "absent").length;
  const pending = students.filter(s => s.status === "pending").length;
  const pct     = total ? Math.round((present / total) * 100) : 0;

  return (
    <div className="wd-root">
      <style>{css}</style>

      {/* OVERRIDE MODAL */}
      {override && (
        <div className="wd-modal-bg">
          <div className="wd-modal">
            <div className="wd-modal-title">Manual Override</div>
            <div className="wd-modal-sub">
              Mark <strong>{override.name}</strong> ({override.roll_no}) as present manually.
              This will be logged with your name.
            </div>
            <label className="wd-modal-label">Reason</label>
            <textarea
              className="wd-modal-textarea"
              rows={3}
              placeholder="e.g. Student was sick, verified in room"
              value={reason}
              onChange={e => setReason(e.target.value)}
            />
            <div className="wd-modal-actions">
              <button className="wd-modal-cancel" onClick={() => { setOverride(null); setReason(""); }}>
                Cancel
              </button>
              <button className="wd-modal-confirm" onClick={submitOverride}>
                Mark Present
              </button>
            </div>
          </div>
        </div>
      )}

      {/* NAV */}
      <nav className="wd-nav">
        <div style={{ display:"flex", alignItems:"center", gap:12 }}>
          <div className="wd-brand">
            <div className="wd-brand-dot" />
            NIT KKR Hostel
          </div>
          <div className="wd-role-badge">Warden</div>
        </div>
        <div className="wd-nav-right">
          <button className="wd-refresh-btn" onClick={loadData}>↻ Refresh</button>
          <button className="wd-logout-btn" onClick={onLogout}>Logout</button>
        </div>
      </nav>

      <div className="wd-main">

        {/* HEADER */}
        <div className="wd-header">
          <div className="wd-header-sub">Warden Dashboard</div>
          <div className="wd-header-title">Today's Attendance</div>
          <div className="wd-header-meta">
            <div className="wd-live-dot" />
            Live · Last updated {lastRefresh.toLocaleTimeString("en-IN", { hour:"2-digit", minute:"2-digit" })}
            &nbsp;·&nbsp; 08:30 – 09:30 AM window
          </div>
        </div>

        {/* STATS */}
        <div className="wd-stats">
          <div className="wd-stat">
            <div className="wd-stat-label">Attendance Rate</div>
            <div className="wd-stat-val teal">{loading ? "—" : pct}<span style={{fontSize:18,color:"var(--muted)"}}>%</span></div>
            <div className="wd-stat-sub">of {total} students</div>
            <div className="wd-stat-bar">
              <div className="wd-stat-bar-fill" style={{ width:`${pct}%`, background:"var(--teal)" }} />
            </div>
          </div>
          <div className="wd-stat">
            <div className="wd-stat-label">Present</div>
            <div className="wd-stat-val green">{loading ? "—" : present}</div>
            <div className="wd-stat-sub">Marked attendance</div>
            <div className="wd-stat-bar">
              <div className="wd-stat-bar-fill" style={{ width:`${total ? (present/total)*100 : 0}%`, background:"var(--green)" }} />
            </div>
          </div>
          <div className="wd-stat">
            <div className="wd-stat-label">Absent</div>
            <div className="wd-stat-val red">{loading ? "—" : absent}</div>
            <div className="wd-stat-sub">No mark today</div>
            <div className="wd-stat-bar">
              <div className="wd-stat-bar-fill" style={{ width:`${total ? (absent/total)*100 : 0}%`, background:"var(--red)" }} />
            </div>
          </div>
          <div className="wd-stat">
            <div className="wd-stat-label">Pending</div>
            <div className="wd-stat-val amber">{loading ? "—" : pending}</div>
            <div className="wd-stat-sub">Window still open</div>
            <div className="wd-stat-bar">
              <div className="wd-stat-bar-fill" style={{ width:`${total ? (pending/total)*100 : 0}%`, background:"var(--amber)" }} />
            </div>
          </div>
        </div>

        {/* FILTER ROW */}
        <div className="wd-filter-row">
          <input
            className="wd-search"
            placeholder="Search by name, roll no, room…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          {["all","present","absent","pending"].map(f => (
            <button
              key={f}
              className={`wd-filter-btn ${filter === f ? "active" : ""}`}
              onClick={() => setFilter(f)}
            >
              {f.charAt(0).toUpperCase() + f.slice(1)}
              {f !== "all" && ` (${students.filter(s => s.status === f).length})`}
            </button>
          ))}
        </div>

        {/* TABLE */}
        <div className="wd-table-wrap">
          <table className="wd-table">
            <thead>
              <tr>
                <th>Student</th>
                <th>Room</th>
                <th>Status</th>
                <th>Marked at</th>
                <th>Confidence</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                [1,2,3,4,5].map(i => (
                  <tr key={i}>
                    {[1,2,3,4,5,6].map(j => (
                      <td key={j}>
                        <div className="wd-skeleton" style={{ height:14, width: j===1?"120px":"70px" }} />
                      </td>
                    ))}
                  </tr>
                ))
              ) : displayed.length === 0 ? (
                <tr>
                  <td colSpan={6}>
                    <div className="wd-empty">
                      <div className="wd-empty-icon">🔍</div>
                      No students match your search
                    </div>
                  </td>
                </tr>
              ) : (
                displayed.map(s => (
                  <tr key={s.roll_no}>
                    <td>
                      <div className="wd-name">{s.name}</div>
                      <div className="wd-roll">{s.roll_no}</div>
                    </td>
                    <td><div className="wd-room">{s.room}</div></td>
                    <td><span className={`wd-badge ${s.status}`}>{s.status.charAt(0).toUpperCase()+s.status.slice(1)}</span></td>
                    <td><div className="wd-time">{formatTime(s.marked_at)}</div></td>
                    <td>
                      <div className="wd-conf">
                        <div className="wd-conf-bar">
                          <div className="wd-conf-fill" style={{ width:`${s.confidence*100}%` }} />
                        </div>
                        <div className="wd-conf-pct">
                          {s.confidence ? `${(s.confidence*100).toFixed(0)}%` : "—"}
                        </div>
                      </div>
                    </td>
                    <td>
                      {s.status !== "present" && (
                        <button className="wd-override-btn" onClick={() => setOverride(s)}>
                          Override
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
