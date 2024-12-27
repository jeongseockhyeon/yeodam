document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(form);

        const guideData = {
            name: formData.get("name"),
            birth: formData.get("birth"),
            gender: formData.get("gender"),
            phone: formData.get("phone"),
            bio: formData.get("bio"),
        };

        try {
            const response = await fetch("/api/guides/join", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(guideData),
            });

            if (response.status === 201) {
                const result = await response.json();
                alert(`가이드가 성공적으로 등록되었습니다: ${result.name}`);
                window.location.href = "/sellers/guide-list";
            } else {
                const error = await response.json();
                alert("등록에 실패했습니다. 다시 시도해주세요.");
            }
        } catch (error) {
            alert("오류가 발생했습니다. 다시 시도해주세요.");
            console.error("Error:", error);
        }
    });
});
