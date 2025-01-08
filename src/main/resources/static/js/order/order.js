
const paymentStatusCheckUrl = "/api/payments/check?orderUid=";

async function submitOrder() {

    if (!validateCheckBox()) {
        alert("약관에 동의해주세요");
        return;
    }

    if (!validateFields()) {
        return;
    }

    const form = document.getElementById('order-form');
    const data = new FormData(form);


    try {
        let response = await (await fetch(form.action, {
            method: "POST",
            body: data
        })).json();

        if (response.statusCode === 400 || response.statusCode === 404) {
            alert(response.message);
            return;
        }

        sessionStorage.setItem("orderUid", response.orderUid);


        var IMP = window.IMP;
        IMP.init('imp65451236'); // 아임포트 가맹점 식별코드

        requestPay(
            response.itemName,
            response.price,
            response.orderUid,
            response.username,
            response.email,
            response.phone
        );

    } catch (error) {
        console.log('에러발생 : ' + error);
    }
}

function validateCheckBox() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    let allChecked = true;

    checkboxes.forEach((checkbox) => {
        const label = document.querySelector(`label[for="${checkbox.id}"]`);
        if (!checkbox.checked) {
            label.style.color = 'red';
            allChecked = false;
        } else {
            label.style.color = '';
        }
    });

    return allChecked;
}

function validateFields() {
    const textFields = document.querySelectorAll('input[type="text"]');

    let hasError = false;
    textFields.forEach((field) => {
        if (!field.value.trim()) {
            field.style.borderColor = 'red';
            field.focus();
        } else {
            field.style.borderColor = 'rgb(221, 221, 221)';
            hasError = true;
        }
    });
    return hasError;
}

window.addEventListener("load", () => {
    sessionStorage.removeItem('orderUid');
});

window.addEventListener("beforeunload", () => {
    if (sessionStorage.getItem("orderUid")) {
        let orderUid = sessionStorage.getItem("orderUid");
        fetch(paymentStatusCheckUrl + orderUid, {
                method: "POST"
            }
        );
    }
});