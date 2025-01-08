document.addEventListener("DOMContentLoaded", () => {
    const inquiryListContainer = document.getElementById("inquiry-list");
    const emptyMessage = document.getElementById("empty-message");

    // 문의 데이터를 가져오기
    const fetchInquiries = async () => {
        try {
            const response = await fetch("/inquiries/seller/data", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
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
                                    "Content-Type": "application/json",
                                },
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
                // 답변하기 버튼
                const answerButton = document.createElement("button");
                answerButton.className = "answer-button";
                answerButton.id = `answer-button-${inquiry.id}`;
                answerButton.textContent = "답변하기";
                answerButton.addEventListener("click", () => showAnswerForm(inquiry.id));

                // 답변 입력 폼
                const answerForm = document.createElement("div");
                answerForm.className = "answer-form";
                answerForm.id = `answer-form-${inquiry.id}`;
                answerForm.style.display = "none";
                answerForm.innerHTML = `
                    <input type="text" id="answer-title-${inquiry.id}" placeholder="답변 제목" />
                    <textarea id="answer-content-${inquiry.id}" rows="4" placeholder="답변 내용을 입력하세요"></textarea>
                    <button class="submit-answer" data-id="${inquiry.id}">답변 제출</button>
                `;

                answerForm.querySelector(".submit-answer").addEventListener("click", async () => {
                    const title = document.getElementById(`answer-title-${inquiry.id}`).value;
                    const content = document.getElementById(`answer-content-${inquiry.id}`).value;

                    if (!title || !content) {
                        alert("제목과 내용을 모두 입력해주세요.");
                        return;
                    }

                    try {
                        const response = await fetch(`/inquiries/${inquiry.id}/answer`, {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json"
                            },
                            body: JSON.stringify({ title, content })
                        });

                        if (!response.ok) {
                            throw new Error("답변 등록 실패");
                        }

                        alert("답변이 등록되었습니다.");
                        fetchInquiries();
                    } catch (error) {
                        console.error("Error submitting answer:", error);
                        alert("답변 등록 중 오류가 발생했습니다.");
                    }
                });

                inquiryBox.appendChild(answerButton);
                inquiryBox.appendChild(answerForm);
            }

            inquiryListContainer.appendChild(inquiryBox);
        });
    };

    // 답변 폼
    const showAnswerForm = (inquiryId) => {
        const form = document.querySelector(`#answer-form-${inquiryId}`);
        const answerButton = document.querySelector(`#answer-button-${inquiryId}`);
        if (form.style.display === "none") {
            form.style.display = "block";
            answerButton.textContent = "취소";
        } else {
            form.style.display = "none";
            answerButton.textContent = "답변하기";
        }
    };

    // 초기 로드 시 API 호출
    fetchInquiries();
});