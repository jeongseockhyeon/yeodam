document.addEventListener("DOMContentLoaded", () => {
    const inquiryListContainer = document.getElementById("inquiry-list");
    const emptyMessage = document.getElementById("empty-message");

    // 문의 데이터를 가져오기
    const fetchInquiries = async () => {
        try {
            const response = await fetch("/inquiries/user/data", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            if (!response.ok) {
                throw new Error("Failed to fetch inquiries");
            }

            const inquiries = await response.json();

            if (inquiries.length === 0) {
                emptyMessage.style.display = "block";
                inquiryListContainer.style.display = "none";
            } else {
                emptyMessage.style.display = "none";
                inquiryListContainer.style.display = "block";
                renderInquiries(inquiries);
            }
        } catch (error) {
            console.error("Error fetching inquiries:", error);
            emptyMessage.style.display = "block";
            emptyMessage.textContent = "문의 내역을 가져오는 중 오류가 발생했습니다.";
        }
    };

    // HTML로 렌더링
    const renderInquiries = (inquiries) => {
        inquiryListContainer.innerHTML = "";

        inquiries.forEach((inquiry) => {
            const inquiryBox = document.createElement("div");
            inquiryBox.className = "inquiry-box";

            const itemName = document.createElement("div");
            itemName.className = "inquiry-itemName";
            itemName.textContent = inquiry.itemName;

            itemName.addEventListener("click", () => {
                window.location.href = `/tours/${inquiry.itemId}`;
            });

            const title = document.createElement("div");
            title.className = "inquiry-title";
            title.textContent = inquiry.title;

            const content = document.createElement("div");
            content.className = "inquiry-content";
            content.textContent = inquiry.content;

            const answerStatus = document.createElement("div");
            answerStatus.className = "answer-status";
            answerStatus.textContent = inquiry.isAnswered === "Y" ? "답변 완료" : "답변 대기";
            if (inquiry.isAnswered === "N") {
                answerStatus.classList.add("pending");
            }

            inquiryBox.appendChild(itemName);
            inquiryBox.appendChild(title);
            inquiryBox.appendChild(content);
            inquiryBox.appendChild(answerStatus);

            if (inquiry.isAnswered === "Y") {
                // 답변 보기 버튼
                const toggleAnswerButton = document.createElement("button");
                toggleAnswerButton.className = "toggle-answer-button";
                toggleAnswerButton.textContent = "답변 보기";

                const answerContainer = document.createElement("div");
                answerContainer.className = "answer-container";
                answerContainer.style.display = "none";

                toggleAnswerButton.addEventListener("click", async () => {
                    if (answerContainer.style.display === "none") {
                        try {
                            const response = await fetch(`/inquiries/${inquiry.id}/answer`, {
                                method: "GET",
                                headers: {
                                    "Content-Type": "application/json"
                                }
                            });

                            if (!response.ok) {
                                throw new Error("Failed to fetch answer");
                            }

                            const answer = await response.json();
                            answerContainer.innerHTML = `
                                <div class='answer-title'>${answer.title}</div>
                                <div class='answer-content'>${answer.content}</div>
                            `;

                            answerContainer.style.display = "block";
                            toggleAnswerButton.textContent = "답변 접기";
                        } catch (error) {
                            console.error("Error fetching answer:", error);
                            alert("답변을 가져오는 중 오류가 발생했습니다.");
                        }
                    } else {
                        answerContainer.style.display = "none";
                        toggleAnswerButton.textContent = "답변 보기";
                    }
                });

                inquiryBox.appendChild(toggleAnswerButton);
                inquiryBox.appendChild(answerContainer);
            }
            else {
                // 삭제 버튼
                const deleteButton = document.createElement("button");
                deleteButton.className = "delete-button";
                deleteButton.textContent = "삭제";
                deleteButton.addEventListener("click", async () => {
                    if (confirm("이 문의를 삭제하시겠습니까?")) {
                        try {
                            const response = await fetch(`/inquiries/${inquiry.id}`, {
                                method: "DELETE",
                            });
                            if (!response.ok) {
                                throw new Error("삭제 실패");
                            }
                            alert("문의가 삭제되었습니다.");
                            fetchInquiries();
                        } catch (error) {
                            console.error("Error deleting inquiry:", error);
                            alert("삭제 중 오류가 발생했습니다.");
                        }
                    }
                });

                inquiryBox.appendChild(deleteButton);
            }

            inquiryListContainer.appendChild(inquiryBox);
        });
    };

    // 초기 로드 시 API 호출
    fetchInquiries();
});