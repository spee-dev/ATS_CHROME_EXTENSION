(function () {
  console.log("Auto-theme widget loaded");

  let fab = null;
  let card = null;

  if (document.getElementById("ai-fab")) return;

  /* ---------------- Detect real website background ---------------- */
  const getPageTheme = () => {
    const body = window.getComputedStyle(document.body).backgroundColor;
    const html = window.getComputedStyle(document.documentElement).backgroundColor;

    const toRGB = (c) => {
      if (!c) return [255, 255, 255];
      const m = c.match(/\d+/g);
      return m ? m.map(Number) : [255, 255, 255];
    };

    const avg = (arr1, arr2) => 
      arr1.map((v, i) => Math.round((v + arr2[i]) / 2));

    const bodyRGB = toRGB(body);
    const htmlRGB = toRGB(html);
    const finalRGB = avg(bodyRGB, htmlRGB);

    const brightness =
      (finalRGB[0] * 299 + finalRGB[1] * 587 + finalRGB[2] * 114) / 1000;

    return brightness < 130 ? "dark" : "light";
  };

  const themeMode = () => getPageTheme();

  const theme = {
    light: {
      bg: "rgba(255,255,255,0.92)",
      border: "rgba(0,0,0,0.12)",
      shadow: "0 10px 34px rgba(0,0,0,0.20)"
    },
    dark: {
      bg: "rgba(30,30,30,0.90)",
      border: "rgba(255,255,255,0.12)",
      shadow: "0 10px 40px rgba(0,0,0,0.55)"
    }
  };

  const t = () => theme[themeMode()];

  /* ---------------- Floating Button ---------------- */
  fab = document.createElement("div");
  fab.id = "ai-fab";
  fab.innerHTML = `
    <svg width="20" height="20" viewBox="0 0 24 24" fill="white">
      <path d="M12 2L15 8L22 9L17 14L18 21L12 18L6 21L7 14L2 9L9 8Z"/>
    </svg>
  `;

  Object.assign(fab.style, {
    position: "fixed",
    bottom: "26px",
    right: "26px",
    width: "50px",
    height: "50px",
    background: "#2563eb",
    borderRadius: "50%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    cursor: "pointer",
    zIndex: "999999",
    boxShadow: "0 6px 24px rgba(0,0,0,0.25)",
    transition: "all 0.2s ease"
  });

  fab.onmouseover = () => fab.style.transform = "scale(1.1)";
  fab.onmouseout = () => fab.style.transform = "scale(1)";
  fab.onmousedown = () => fab.style.transform = "scale(0.9)";
  fab.onmouseup = () => fab.style.transform = "scale(1.1)";

  /* ---------------- Popup Card ---------------- */
  card = document.createElement("iframe");
  card.id = "ai-card";
  card.src = chrome.runtime.getURL("sidebar/build/index.html");

  const applyTheme = () => {
    Object.assign(card.style, {
      background: t().bg,
      border: `1px solid ${t().border}`,
      boxShadow: t().shadow
    });
  };

  Object.assign(card.style, {
    position: "fixed",
    top: "16px",
    right: "16px",
    width: "380px",
    height: "calc(100vh - 32px)",
    borderRadius: "18px",
    display: "none",
    opacity: "0",
    transition: "opacity 0.25s ease, transform 0.25s ease",
    zIndex: "999999",
    transform: "translateX(20px)"
  });

  applyTheme();

  document.body.appendChild(fab);
  document.body.appendChild(card);

  /* ---------------- Toggle open/close ---------------- */
  fab.onclick = () => {
    applyTheme(); // update theme every time
    if (card.style.display === "none") {
      card.style.display = "block";
      fab.style.display = "none"; // Hide FAB when sidebar opens
      setTimeout(() => {
        card.style.opacity = "1";
        card.style.transform = "translateX(0)";
      }, 10);
    } else {
      card.style.opacity = "0";
      card.style.transform = "translateX(20px)";
      fab.style.display = "flex"; // Show FAB when sidebar closes
      setTimeout(() => (card.style.display = "none"), 250);
    }
  };

  /* ---------------- Close on outside click ---------------- */
  document.addEventListener("click", (event) => {
    if (!card.contains(event.target) && !fab.contains(event.target)) {
      card.style.opacity = "0";
      card.style.transform = "translateX(20px)";
      fab.style.display = "flex"; // Show FAB when sidebar closes
      setTimeout(() => (card.style.display = "none"), 250);
    }
  });

  /* ---------------- X-FRAME MESSAGING ---------------- */
  window.onmessage = (event) => {
    // Check if the message is from the sidebar
    if (event.source !== card.contentWindow) return;

    const { type, data } = event.data;

    if (type === "REQUEST_JD") {
      // More robust JD extraction
      const selectors = [
        "#job-details",
        ".job-description",
        ".job-details__main-content",
        "[class*='job-description']",
        "[class*='job-details']",
      ];

      let jdContainer = null;
      for (const selector of selectors) {
        jdContainer = document.querySelector(selector);
        if (jdContainer) break;
      }

      const jobDescription = jdContainer
        ? jdContainer.innerText
        : "Could not extract Job Description.";

      card.contentWindow.postMessage({ type: "JOB_DESCRIPTION", data: jobDescription }, "*");
    }

    if (type === "CLOSE_SIDEBAR") {
      card.style.opacity = "0";
      card.style.transform = "translateX(20px)";
      fab.style.display = "flex"; // Show FAB when sidebar closes
      setTimeout(() => (card.style.display = "none"), 250);
    }
  };

})();
