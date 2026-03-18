document.addEventListener("DOMContentLoaded", function () {

// Live Search
    const searchInput = document.getElementById("invoiceSearch");
    const tableRows = document.querySelectorAll("#invoicesTable tbody tr");

    if (!searchInput) return; // safety

    searchInput.addEventListener("keyup", function () {

        const searchTerm = searchInput.value.toLowerCase();

        tableRows.forEach(row => {

            const rowText = row.textContent.toLowerCase();

            row.style.display = rowText.includes(searchTerm) ? "" : "none";

        });

    });

});