// helper to call the backend, throws the error message from the server
async function api(url, method, body) {
    const options = { method: method || "GET", headers: {} };
    if (body) {
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(body);
    }

    const response = await fetch(url, options);
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data && data.message ? data.message : "Something went wrong");
    }
    return data;
}

// stops names with < > from breaking the page
function escapeHtml(text) {
    if (text == null) return "";
    return String(text)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function formatDateTime(value) {
    if (!value) return "";
    return value.replace("T", " ").substring(0, 16);
}

function formatCategory(category) {
    if (category === "WALK_IN") return "Walk-in";
    if (category === "APPOINTMENT") return "Appointment";
    return "Emergency";
}
