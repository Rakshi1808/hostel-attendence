import { useState, useEffect } from "react";

const API_BASE = "http://localhost:8080";

const css = `
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Space+Grotesk:wght@500;600;700&display=swap');

  .ad-root *{box-sizing:border-box;margin:0;padding:0}
  .ad-root{
    --navy:#0F1B2D;--navy2:#1A2D45;--navy3:#243B55;
    --teal:#00C9B1;--teal2:#00A896;
    --amber:#F59E0B;--green:#10B981;--red:#EF4444;
    --purple:#818CF8;
    --white:#F8FAFC;--muted:#94A3B8;--border:rgba(255,255,255,0.08);
    --card:rgba(26,45,69,0.8);
    background:var(--navy);font-family:'Inter',sans-serif;
    color:var(--white);min-height:100vh;display:flex;
  }

  /* SIDEBAR */
  .ad-sidebar{width:220px;min-height:100vh;background:var(--navy2);
    border-right:1px solid var(--border);display:flex;
    flex-direction:column;padding:20px 0;flex-shrink:0;
    position:sticky;top:0;height:100vh;overflow-y:auto}
  .ad-sidebar-brand{padding:0 20px 20px;border-bottom:1px solid var(--border);
    margin-bottom:12px}
  .ad-brand{font-family:'Space Grotesk',sans-serif;font-weight:700;
    font-size:15px;display:flex;align-items:center;gap:8px}
  .ad-brand-dot{width:8px;height:8px;border-radius:50%;
    background:var(--teal);box-shadow:0 0 8px var(--teal)}
  .ad-brand-sub{font-size:11px;color:var(--muted);margin-top:3px}
  .ad-nav-item{display:flex;align-items:center;gap:10px;padding:10px 20px;
    cursor:pointer;font-size:13px;color:var(--muted);transition:all 0.2s;
    border-left:2px solid transparent}
  .ad-nav-item:hover{color:var(--white);background:rgba(255,255,255,0.04)}
  .ad-nav-item.active{color:var(--teal);border-left-color:var(--teal);
    background:rgba(0,201,177,0.06)}
  .ad-nav-icon{font-size:16px;width:20px;text-align:center}
  .ad-sidebar-footer{margin-top:auto;padding:16px 20px;
    border-top:1px solid var(--border)}
  .ad-logout{font-size:13px;color:var(--muted);cursor:pointer;
    display:flex;align-items:center;gap:8px}
  .ad-logout:hover{color:var(--red)}

  /* MAIN CONTENT */
  .ad-content{flex:1;overflow-y:auto}
  .ad-topbar{padding:16px 28px;border-bottom:1px solid var(--border);
    display:flex;align-items:center;justify-content:space-between;
    background:rgba(15,27,45,0.9);position:sticky;top:0;z-index:10;
    backdrop-filter:blur(10px)}
  .ad-topbar-title{font-family:'Space Grotesk',sans-serif;font-size:17px;font-weight:600}
  .ad-topbar-right{display:flex;align-items:center;gap:10px}
  .ad-date{font-size:12px;color:var(--muted)}
  .ad-avatar{width:34px;height:34px;border-radius:50%;
    background:linear-gradient(135deg,var(--purple),var(--teal));
    display:flex;align-items:center;justify-content:center;
    font-weight:600;font-size:12px;color:var(--white)}

  .ad-body{padding:24px 28px 40px}

  /* STATS GRID */
  .ad-stats{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-bottom:28px}
  @media(max-width:900px){.ad-stats{grid-template-columns:repeat(2,1fr)}}
  .ad-stat{background:var(--card);border:1px solid var(--border);
    border-radius:14px;padding:18px;backdrop-filter:blur(10px);
    position:relative;overflow:hidden}
  .ad-stat::before{content:'';position:absolute;top:0;left:0;right:0;
    height:2px}
  .ad-stat.teal::before{background:var(--teal)}
  .ad-stat.green::before{background:var(--green)}
  .ad-stat.amber::before{background:var(--amber)}
  .ad-stat.purple::before{background:var(--purple)}
  .ad-stat-icon{font-size:22px;margin-bottom:10px}
  .ad-stat-label{font-size:11px;color:var(--muted);font-weight:500;
    letter-spacing:0.05em;text-transform:uppercase;margin-bottom:6px}
  .ad-stat-val{font-family:'Space Grotesk',sans-serif;font-size:30px;
    font-weight:700;line-height:1}
  .ad-stat-val.teal{color:var(--teal)}
  .ad-stat-val.green{color:var(--green)}
  .ad-stat-val.amber{color:var(--amber)}
  .ad-stat-val.purple{color:var(--purple)}
  .ad-stat-sub{font-size:11px;color:var(--muted);margin-top:5px}

  /* TWO COL LAYOUT */
  .ad-two-col{display:grid;grid-template-columns:1fr 1fr;gap:16px;margin-bottom:20px}
  @media(max-width:800px){.ad-two-col{grid-template-columns:1fr}}

  /* CARD */
  .ad-card{background:var(--card);border:1px solid var(--border);
    border-radius:14px;overflow:hidden;backdrop-filter:blur(10px)}
  .ad-card-header{padding:16px 18px;border-bottom:1px solid var(--border);
    display:flex;align-items:center;justify-content:space-between}
  .ad-card-title{font-family:'Space Grotesk',sans-serif;font-size:14px;font-weight:600}
  .ad-card-action{font-size:12px;color:var(--teal);cursor:pointer;font-weight:500}

  /* HOSTEL LIST */
  .ad-hostel-row{display:flex;align-items:center;gap:12px;padding:12px 18px;
    border-bottom:1px solid var(--border)}
  .ad-hostel-row:last-child{border-bottom:none}
  .ad-hostel-name{font-size:13px;font-weight:500;flex:1}
  .ad-hostel-pct{font-size:13px;font-weight:600;min-width:40px;text-align:right}
  .ad-hostel-bar{flex:2;height:4px;background:rgba(255,255,255,0.08);
    border-radius:2px;overflow:hidden}
  .ad-hostel-fill{height:4px;border-radius:2px;background:var(--teal);
    transition:width 0.8s ease}
  .ad-hostel-badge{font-size:10px;padding:2px 7px;border-radius:10px;
    font-weight:500;margin-left:8px}
  .ad-hostel-badge.girls{background:rgba(129,140,248,0.15);color:var(--purple)}
  .ad-hostel-badge.boys{background:rgba(0,201,177,0.1);color:var(--teal)}

  /* RECENT ACTIVITY */
  .ad-activity-row{display:flex;align-items:flex-start;gap:12px;
    padding:12px 18px;border-bottom:1px solid var(--border)}
  .ad-activity-row:last-child{border-bottom:none}
  .ad-activity-icon{width:32px;height:32px;border-radius:50%;
    display:flex;align-items:center;justify-content:center;
    font-size:14px;flex-shrink:0;margin-top:1px}
  .ad-activity-icon.green{background:rgba(16,185,129,0.15)}
  .ad-activity-icon.amber{background:rgba(245,158,11,0.12)}
  .ad-activity-icon.red{background:rgba(239,68,68,0.12)}
  .ad-activity-icon.teal{background:rgba(0,201,177,0.1)}
  .ad-activity-body{flex:1}
  .ad-activity-text{font-size:13px;color:var(--white)}
  .ad-activity-time{font-size:11px;color:var(--muted);margin-top:2px}

  /* QUICK ACTIONS */
  .ad-actions-grid{display:grid;grid-template-columns:1fr 1fr;gap:10px;padding:16px 18px}
  .ad-action-btn{padding:12px;background:var(--navy3);border:1px solid var(--border);
    border-radius:10px;cursor:pointer;text-align:left;transition:all 0.2s}
  .ad-action-btn:hover{border-color:var(--teal);background:rgba(0,201,177,0.05)}
  .ad-action-btn-icon{font-size:18px;margin-bottom:6px}
  .ad-action-btn-label{font-size:12px;font-weight:500;color:var(--white)}
  .ad-action-btn-sub{font-size:11px;color:var(--muted);margin-top:2px}

  /* TABLE */
  .ad-table-wrap{background:var(--card);border:1px solid var(--border);
    border-radius:14px;overflow:hidden;backdrop-filter:blur(10px);margin-bottom:20px}
  .ad-table{width:100%;border-collapse:collapse}
  .ad-table th{padding:12px 16px;text-align:left;font-size:11px;font-weight:500;
    color:var(--muted);letter-spacing:0.05em;text-transform:uppercase;
    border-bottom:1px solid var(--border);background:rgba(15,27,45,0.5)}
  .ad-table td{padding:12px 16px;font-size:13px;border-bottom:1px solid var(--border)}
  .ad-table tr:last-child td{border-bottom:none}
  .ad-table tr:hover td{background:rgba(255,255,255,0.02)}
  .ad-badge{font-size:11px;font-weight:500;padding:3px 9px;border-radius:20px}
  .ad-badge.active{background:rgba(16,185,129,0.15);color:var(--green);
    border:1px solid rgba(16,185,129,0.2)}
  .ad-badge.pending{background:rgba(245,158,11,0.12);color:var(--amber);
    border:1px solid rgba(245,158,11,0.2)}
  .ad-badge.suspended{background:rgba(239,68,68,0.12);color:var(--red);
    border:1px solid rgba(239,68,68,0.2)}

  /* SKELETON */
  .ad-skeleton{background:linear-gradient(90deg,var(--navy2) 25%,
    var(--navy3) 50%,var(--navy2) 75%);background-size:200% 100%;
    animation:ad-shimmer 1.5s infinite;border-radius:6px}
  @keyframes ad-shimmer{0%{background-position:200% 0}
    100%{background-position:-200% 0}}
`;

// ── DUMMY DATA ─────────────────────────────────────────────
const HOSTELS = [
  { name:"Kalpana Chawla", gender:"girls", pct:91 },
  { name:"Bhagirathi",     gender:"girls", pct:88 },
  { name:"Cauvery",        gender:"girls", pct:76 },
  { name:"Alaknanda",      gender:"girls", pct:83 },
  { name:"H1",  gender:"boys", pct:79 },
  { name:"H2",  gender:"boys", pct:85 },
  { name:"H5",  gender:"boys", pct:92 },
  { name:"H8",  gender:"boys", pct:71 },
];

const ACTIVITIES = [
  { icon:"✓", type:"green", text:"Attendance window opened — all 15 hostels", time:"8:30 AM today" },
  { icon:"👤", type:"teal", text:"New student registered — 525410044 (H5)", time:"8:12 AM today" },
  { icon:"⚠️", type:"amber", text:"Low attendance alert — Cauvery hostel (76%)", time:"9:31 AM today" },
  { icon:"✎", type:"amber", text:"Warden override — 525410039 marked present", time:"9:45 AM today" },
  { icon:"✓", type:"green", text:"Attendance window closed — 78% overall", time:"9:30 AM today" },
];

const RECENT_STUDENTS = [
  { roll_no:"525410044", name:"Meera Joshi",  hostel:"Kalpana Chawla", programme:"Btech", status:"pending" },
  { roll_no:"525410045", name:"Aarav Shah",   hostel:"H3",             programme:"MCA",   status:"active" },
  { roll_no:"525410046", name:"Tanvi Gupta",  hostel:"Bhagirathi",     programme:"Mtech", status:"pending" },
  { roll_no:"525410047", name:"Siddharth Rao",hostel:"H7",             programme:"MBA",   status:"active" },
];

const NAV_ITEMS = [
  { id:"dashboard",   icon:"🏠", label:"Dashboard" },
  { id:"hostels",     icon:"🏢", label:"Hostel Management" },
  { id:"wardens",     icon:"👮", label:"Warden Management" },
  { id:"students",    icon:"🎓", label:"Student Management" },
  { id:"attendance",  icon:"📋", label:"Attendance" },
  { id:"reports",     icon:"📊", label:"Reports & Analytics" },
  { id:"settings",    icon:"⚙️", label:"System Settings" },
];

export default function AdminDashboard({ onLogout }) {
  const [activeNav, setActiveNav] = useState("dashboard");
  const [stats, setStats]         = useState(null);
  const [loading, setLoading]     = useState(true);

  const token   = localStorage.getItem("token");
  const headers = { "Content-Type":"application/json", Authorization:`Bearer ${token}` };

  useEffect(() => { loadStats(); }, []);

  async function loadStats() {
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/admin/stats`, { headers });
      if (res.ok) setStats(await res.json());
      else setStats({ hostels:15, students:1248, today_pct:78, wardens:22 });
    } catch {
      setStats({ hostels:15, students:1248, today_pct:78, wardens:22 });
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="ad-root">
      <style>{css}</style>

      {/* SIDEBAR */}
      <div className="ad-sidebar">
        <div className="ad-sidebar-brand">
          <div className="ad-brand">
            <div className="ad-brand-dot" />
            NIT KKR Hostel
          </div>
          <div className="ad-brand-sub">Admin Panel</div>
        </div>

        {NAV_ITEMS.map(item => (
          <div
            key={item.id}
            className={`ad-nav-item ${activeNav === item.id ? "active" : ""}`}
            onClick={() => setActiveNav(item.id)}
          >
            <span className="ad-nav-icon">{item.icon}</span>
            {item.label}
          </div>
        ))}

        <div className="ad-sidebar-footer">
          <div className="ad-logout" onClick={onLogout}>
            <span>🚪</span> Logout
          </div>
        </div>
      </div>

      {/* CONTENT */}
      <div className="ad-content">

        {/* TOPBAR */}
        <div className="ad-topbar">
          <div className="ad-topbar-title">
            {NAV_ITEMS.find(n => n.id === activeNav)?.label}
          </div>
          <div className="ad-topbar-right">
            <div className="ad-date">
              {new Date().toLocaleDateString("en-IN", { weekday:"long", day:"numeric", month:"long", year:"numeric" })}
            </div>
            <div className="ad-avatar">AD</div>
          </div>
        </div>

        <div className="ad-body">

          {/* STATS */}
          <div className="ad-stats">
            <div className="ad-stat teal">
              <div className="ad-stat-icon">🏢</div>
              <div className="ad-stat-label">Total Hostels</div>
              <div className="ad-stat-val teal">{loading ? "—" : stats?.hostels ?? 15}</div>
              <div className="ad-stat-sub">4 girls · 11 boys</div>
            </div>
            <div className="ad-stat green">
              <div className="ad-stat-icon">🎓</div>
              <div className="ad-stat-label">Total Students</div>
              <div className="ad-stat-val green">{loading ? "—" : (stats?.students ?? 1248).toLocaleString()}</div>
              <div className="ad-stat-sub">Active accounts</div>
            </div>
            <div className="ad-stat amber">
              <div className="ad-stat-icon">📋</div>
              <div className="ad-stat-label">Today's Attendance</div>
              <div className="ad-stat-val amber">{loading ? "—" : stats?.today_pct ?? 78}<span style={{fontSize:18,color:"var(--muted)"}}>%</span></div>
              <div className="ad-stat-sub">Across all hostels</div>
            </div>
            <div className="ad-stat purple">
              <div className="ad-stat-icon">👮</div>
              <div className="ad-stat-label">Wardens</div>
              <div className="ad-stat-val purple">{loading ? "—" : stats?.wardens ?? 22}</div>
              <div className="ad-stat-sub">Active this month</div>
            </div>
          </div>

          {/* TWO COL */}
          <div className="ad-two-col">

            {/* HOSTEL WISE */}
            <div className="ad-card">
              <div className="ad-card-header">
                <div className="ad-card-title">Hostel-wise Attendance</div>
                <div className="ad-card-action">View all →</div>
              </div>
              {HOSTELS.map(h => (
                <div key={h.name} className="ad-hostel-row">
                  <div className="ad-hostel-name">
                    {h.name}
                    <span className={`ad-hostel-badge ${h.gender}`}>{h.gender}</span>
                  </div>
                  <div className="ad-hostel-bar">
                    <div className="ad-hostel-fill" style={{ width:`${h.pct}%` }} />
                  </div>
                  <div className="ad-hostel-pct"
                    style={{ color: h.pct >= 85 ? "var(--green)" : h.pct >= 75 ? "var(--amber)" : "var(--red)" }}>
                    {h.pct}%
                  </div>
                </div>
              ))}
            </div>

            {/* RIGHT COL */}
            <div style={{ display:"flex", flexDirection:"column", gap:16 }}>

              {/* QUICK ACTIONS */}
              <div className="ad-card">
                <div className="ad-card-header">
                  <div className="ad-card-title">Quick Actions</div>
                </div>
                <div className="ad-actions-grid">
                  {[
                    { icon:"👤", label:"Add Student",    sub:"Register new student" },
                    { icon:"👮", label:"Add Warden",     sub:"Assign hostel warden" },
                    { icon:"📢", label:"Announcement",   sub:"Notify all students" },
                    { icon:"📄", label:"Generate Report",sub:"Export attendance CSV" },
                  ].map(a => (
                    <div key={a.label} className="ad-action-btn"
                      onClick={() => alert(`${a.label} — coming soon`)}>
                      <div className="ad-action-btn-icon">{a.icon}</div>
                      <div className="ad-action-btn-label">{a.label}</div>
                      <div className="ad-action-btn-sub">{a.sub}</div>
                    </div>
                  ))}
                </div>
              </div>

              {/* RECENT ACTIVITY */}
              <div className="ad-card">
                <div className="ad-card-header">
                  <div className="ad-card-title">Recent Activity</div>
                  <div className="ad-card-action">View logs →</div>
                </div>
                {ACTIVITIES.map((a, i) => (
                  <div key={i} className="ad-activity-row">
                    <div className={`ad-activity-icon ${a.type}`}>{a.icon}</div>
                    <div className="ad-activity-body">
                      <div className="ad-activity-text">{a.text}</div>
                      <div className="ad-activity-time">{a.time}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* RECENT STUDENTS */}
          <div className="ad-card-header" style={{ padding:"0 0 14px" }}>
            <div className="ad-card-title" style={{ fontFamily:"'Space Grotesk',sans-serif", fontSize:15 }}>
              Recently Registered Students
            </div>
            <div className="ad-card-action">View all →</div>
          </div>
          <div className="ad-table-wrap">
            <table className="ad-table">
              <thead>
                <tr>
                  <th>Roll No</th>
                  <th>Name</th>
                  <th>Hostel</th>
                  <th>Programme</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {RECENT_STUDENTS.map(s => (
                  <tr key={s.roll_no}>
                    <td style={{ fontFamily:"'Space Grotesk',sans-serif", color:"var(--teal)", fontSize:12 }}>
                      {s.roll_no}
                    </td>
                    <td style={{ fontWeight:500 }}>{s.name}</td>
                    <td style={{ color:"var(--muted)" }}>{s.hostel}</td>
                    <td style={{ color:"var(--muted)" }}>{s.programme}</td>
                    <td><span className={`ad-badge ${s.status}`}>{s.status.charAt(0).toUpperCase()+s.status.slice(1)}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

        </div>
      </div>
    </div>
  );
}
