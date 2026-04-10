console.log("Receipts JS loaded");

document.addEventListener("DOMContentLoaded", function () {

    // Receipt Table Search
    const tableSearch = document.getElementById("receiptSearch");
    const table = document.querySelector("table");
    const tableBody = document.querySelector("table tbody");

    if (tableSearch && table && tableBody) {

        const rows = tableBody.querySelectorAll("tr");

        tableSearch.addEventListener("input", function () {

            const filter = this.value.toLowerCase();

            rows.forEach(row => {

                if (row.querySelector("td[colspan]")) return;

                let match = false;

                row.querySelectorAll("td").forEach(td => {
                    if (td.textContent.toLowerCase().includes(filter)) {
                        match = true;
                    }
                });

                row.style.display = match ? "" : "none";
            });

        });
    }

    // Invoice Search
    const invoiceInput = document.getElementById("invoiceInput");
    const invoiceIdInput = document.getElementById("invoiceId");
    const suggestions = document.getElementById("invoiceSuggestions");

    const totalField = document.getElementById("invoiceTotal");
    const outstandingField = document.getElementById("outstandingBalance");
    const amountPaidField = document.getElementById("amountPaid");
    const discountField = document.getElementById("discount");

    if (!invoiceInput || !suggestions) return;

    let debounceTimer;

    // Detect edit mode
    const isEdit = amountPaidField && amountPaidField.value && amountPaidField.value !== "0";

    invoiceInput.addEventListener("input", function () {

        const query = this.value;

        // reset ID when typing
        invoiceIdInput.value = "";

        clearTimeout(debounceTimer);

        debounceTimer = setTimeout(() => {

            if (query.length < 2) {
                suggestions.innerHTML = "";
                return;
            }

            fetch("/api/invoices/search?query=" + encodeURIComponent(query))
                .then(res => res.json())
                .then(data => {

                    suggestions.innerHTML = "";

                    data.forEach(item => {

                        const li = document.createElement("li");
                        li.className = "list-group-item list-group-item-action";
                        li.style.cursor = "pointer";

                        li.textContent =
                            item.invoiceNumber + " - " +
                            item.projectTitle + " (€" + item.amount + ")";

                        li.onclick = () => {
                            invoiceInput.value = item.invoiceNumber;
                            invoiceIdInput.value = item.id;
                            suggestions.innerHTML = "";


                            loadInvoiceSummary(item.id);
                        };

                        suggestions.appendChild(li);
                    });
                });

        }, 300);
    });

    // Invoice Autofill Logic
    function loadInvoiceSummary(invoiceId) {

        if (!invoiceId) return;

        console.log("Loading invoice:", invoiceId);

        fetch(`/api/invoices/${invoiceId}/summary`)
            .then(res => res.json())
            .then(data => {

                console.log("Invoice summary:", data);

                const total = parseFloat(data.grossTotal || 0);
                const outstanding = parseFloat(data.outstanding || 0);

                if (totalField) totalField.value = total.toFixed(2);
                if (outstandingField) outstandingField.value = outstanding.toFixed(2);

                // Only autofill on create
                if (!isEdit) {
                    if (discountField) discountField.value = "0.00";
                    updateAmountPaid();
                }

            })
            .catch(err => console.error("Autofill error:", err));
    }

    function updateAmountPaid() {
        if (!outstandingField || !amountPaidField || !discountField) return;

        const outstanding = parseFloat(outstandingField.value) || 0;
        const discount = parseFloat(discountField.value) || 0;

        const amountPaid = outstanding - discount;

        amountPaidField.value = amountPaid.toFixed(2);
    }

    if (discountField) {
        discountField.addEventListener("input", updateAmountPaid);
    }

    if (amountPaidField) {
        amountPaidField.addEventListener("input", updateOutstanding);
    }

    setTimeout(() => {
        if (invoiceIdInput && invoiceIdInput.value) {
            loadInvoiceSummary(invoiceIdInput.value);
        }
    }, 100);

    function updateOutstanding() {
        if (!outstandingField || !amountPaidField || !discountField) return;

        const total = parseFloat(totalField.value) || 0;
        const amountPaid = parseFloat(amountPaidField.value) || 0;
        const discount = parseFloat(discountField.value) || 0;

        const outstanding = total - (amountPaid + discount);

        outstandingField.value = Math.max(outstanding, 0).toFixed(2);
    }

});