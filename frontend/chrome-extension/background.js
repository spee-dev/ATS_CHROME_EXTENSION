chrome.runtime.onMessage.addListener((msg, sender, sendResponse) => {
  if (msg.type === "OPEN_SIDEBAR") {
    chrome.scripting.executeScript({
      target: { tabId: sender.tab.id },
      files: ["contentScript.js"]
    });
  }
  sendResponse(true);
});
