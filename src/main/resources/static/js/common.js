window.addEventListener("error", event => {
    alert(event.error?.message || "오류가 발생했습니다.");
});

window.addEventListener("unhandledrejection", event => {
    alert(event.reason?.message || "오류가 발생했습니다.");
});