document.addEventListener("DOMContentLoaded", () => {

    let deleteYear = null;
    let deleteMonth = null;
    let deleteDay = null;

    // 계정 삭제되는 날짜 가져오기
    fetch("/api/auth/get-date", {
        method: "GET",
    })
        .then(response => {
            if (!response.ok) {

                throw new Error("HTTP error: " + response.status);
            }

            return response.json();
        })
        .then(data => {
            console.log("Response: ", data);

            deleteYear = data.year;
            deleteMonth = data.month;
            deleteDay = data.day;
            document.getElementById("year").innerText = data.year;
            document.getElementById("month").innerText = data.month;
            document.getElementById("day").innerText = data.day;
        })
        .catch(error => {
            console.error("Error: ", error);
        });

    document.getElementById("deleteBtn").addEventListener("click", () => {

        fetch("/api/auth/expiration", {
            method: "POST",
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP error: " + response.status);
                }

                window.location.href = "/logout";
            })
            .catch(error => {
                console.error("Error: ", error);
            });
    })
})