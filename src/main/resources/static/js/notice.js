console.log("notice.js loaded");

window.onload = function() {
    console.log("window loaded");
    Notification.requestPermission();
};

function checkNotification() {


    const startElement = document.getElementById("startNoticeTime");
    const endElement = document.getElementById("endNoticeTime");

    if (!startElement || !endElement){
        return;
    }

    const startNoticeTime = startElement.value;
    const endNoticeTime = endElement.value;

    const now = new Date();

    const hh =
        String(now.getHours()).padStart(2, "0");

    const mm =
        String(now.getMinutes()).padStart(2, "0");

    const currentTime = `${hh}:${mm}`;

    if(Notification.permission === "granted"
        &&
        currentTime === startNoticeTime){
        new Notification(
            "Smart Time Card",
            {
                body: "まもなく出勤時間です"
            }
        );
    }

    if(Notification.permission === "granted"
        &&
        currentTime === endNoticeTime){
        new Notification(
            "Smart Time Card",
            {
                body: "まもなく退勤時間です"
            }
        );
    }

}

setInterval(
    checkNotification,
    1000
);
