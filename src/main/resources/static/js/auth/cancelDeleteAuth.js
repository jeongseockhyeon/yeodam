document.getElementById("cancelDeleteBtn").addEventListener("click", () => {

    fetch("/api/auth/expiration", {
        method: "PUT",
    })
        .then(response=> {
            if (!response.ok) {throw new Error("HTTP error: " + response.status);}

        })
        .catch(error => {
            console.error("Error: ", error);
        });
})