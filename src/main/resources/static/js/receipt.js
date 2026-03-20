console.log("Receipts JS loaded");

document.addEventListener("DOMContentLoaded", function () {

   // Receipt Search
    const container = document.querySelector(".container");
    const table = document.querySelector("table");
    const tableBody = document.querySelector("table tbody");

    if (container && table && tableBody) {

        const tableSearch = document.createElement("input");
        tableSearch.type = "text";
        tableSearch.placeholder = "Search receipts...";
        tableSearch.className = "form-control mb-3";

        container.insertBefore(tableSearch, table.parentElement);

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

     //  Invoice autofills receipt form
     const invoiceSelect = document.getElementById("invoiceSelect");

     const totalField = document.getElementById("invoiceTotal");
     const outstandingField = document.getElementById("outstandingBalance");
     const amountPaidField = document.getElementById("amountPaid");

     if (!invoiceSelect) return;

     // Detect edit mode
     const isEdit = amountPaidField && amountPaidField.value && amountPaidField.value !== "0";

     // Function to load invoice data
     function loadInvoiceSummary(invoiceId) {

         if (!invoiceId) return;

         console.log("Loading invoice:", invoiceId);

         fetch(`/api/invoices/${invoiceId}/summary`)
             .then(res => res.json())
             .then(data => {

                 console.log("Invoice summary:", data);

                 const total = parseFloat(data.grossTotal || 0);
                 const outstanding = parseFloat(data.outstanding || 0);

                 // Always update these
                 if (totalField) totalField.value = total.toFixed(2);
                 if (outstandingField) outstandingField.value = outstanding.toFixed(2);

                 // ONLY autofill amount on CREATE
                 if (!isEdit && amountPaidField) {
                     amountPaidField.value = outstanding.toFixed(2);
                 }

             })
             .catch(err => console.error("Autofill error:", err));
     }

     // Change event (user selects invoice)
     invoiceSelect.addEventListener("change", function () {
         loadInvoiceSummary(this.value);
     });

     // Run on page load (important for edit form)
     setTimeout(() => {
         loadInvoiceSummary(invoiceSelect.value);
     }, 100);
   });