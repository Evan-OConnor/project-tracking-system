document.addEventListener("DOMContentLoaded", function () {

    const invoiceSelect = document.querySelector("[name='invoiceId']");
    const totalField = document.getElementById("invoiceTotal");
    const outstandingField = document.getElementById("outstandingBalance");
    const discountField = document.querySelector("[name='discount']");
    const amountPaidField = document.querySelector("[name='amountPaid']");

    function loadInvoicePaymentInfo() {

        const invoiceId = invoiceSelect.value;

        if (!invoiceId) return;

        fetch(`/invoices/api/${invoiceId}/payment-info`)
            .then(response => response.json())
            .then(data => {

                const total = parseFloat(data.total || 0);
                const discount = parseFloat(data.discount || 0);

                totalField.value = total.toFixed(2);

                if (discountField) {
                    discountField.value = discount.toFixed(2);
                }

                updateOutstanding();
            })
            .catch(err => {
                console.error("Error loading invoice payment info:", err);
            });
    }

    function updateOutstanding() {

        const total = parseFloat(totalField.value || 0);
        const discount = parseFloat(discountField.value || 0);

        let outstanding = total - discount;

        if (outstanding < 0) {
            outstanding = 0;
        }

        outstandingField.value = outstanding.toFixed(2);

        if (amountPaidField) {
            amountPaidField.value = outstanding.toFixed(2);
        }
    }

    if (invoiceSelect) {
        invoiceSelect.addEventListener("change", loadInvoicePaymentInfo);
    }

    if (discountField) {
        discountField.addEventListener("input", updateOutstanding);
    }

});