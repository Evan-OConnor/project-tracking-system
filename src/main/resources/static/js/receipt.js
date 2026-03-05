document.addEventListener("DOMContentLoaded", function () {

    const invoiceSelect = document.querySelector("[name='invoiceId']");
    const totalField = document.getElementById("invoiceTotal");
    const outstandingField = document.getElementById("outstandingBalance");
    const discountField = document.querySelector("[name='discount']");
    const amountPaidField = document.querySelector("[name='amountPaid']");

    function updateInvoiceTotal() {

        const invoiceId = invoiceSelect.value;

        if (!invoiceId) return;

        fetch(`/invoices/api/${invoiceId}/total`)
            .then(response => response.json())
            .then(total => {

                totalField.value = total.toFixed(2);

                updateOutstanding();

            });
    }

    function updateOutstanding() {

        const total = parseFloat(totalField.value || 0);
        const discount = parseFloat(discountField.value || 0);

        const outstanding = total - discount;

        outstandingField.value = outstanding.toFixed(2);

        amountPaidField.value = outstanding.toFixed(2);
    }

    if (invoiceSelect) {
        invoiceSelect.addEventListener("change", updateInvoiceTotal);
    }

    if (discountField) {
        discountField.addEventListener("input", updateOutstanding);
    }

});