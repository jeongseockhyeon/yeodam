document.getElementById("userEditForm").addEventListener("submit", function (event) {

    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);

    const jsonData = {};
    formData.forEach((value, key) => {
        jsonData[key] = value;
    })

    fetch("/api/users", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if(!response.ok) {
                return response.json().then(errorMessages => {
                    console.log("errorMessages: " + errorMessages);
                    showErrorMessages(errorMessages);
                    throw new Error("HTTP error: " + response.status);
                })
            }

            alert("회원 수정이 완료되었습니다!");
            window.location.href = "/users/myPage";
        })
        .catch(error => {
            console.error("Error:", error);
        });
})