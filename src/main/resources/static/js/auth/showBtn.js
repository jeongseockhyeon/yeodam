document.addEventListener("DOMContentLoaded", () => {

    const deleteBtn = document.getElementById("showDeleteBtn");
    const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");

    fetch("/api/auth/expiration", {
        method: "GET",
    })
        .then(response => {
            if (!response.ok) {throw new Error("HTTP error: " + response.status);}

            return response.json();
        })
        .then(isExpired => {
            if (isExpired) {
                cancelDeleteBtn.style.display = "inline-block";
                deleteBtn.style.display = "none";
            } else {
                deleteBtn.style.display = "inline-block";
                cancelDeleteBtn.style.display = "none";
            }
        })
        .catch(error => {
            console.error("Error: ", error);
        });
})
