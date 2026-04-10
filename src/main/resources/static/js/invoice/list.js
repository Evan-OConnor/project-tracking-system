document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("invoiceSearch");
    const tableRows = document.querySelectorAll("#invoicesTable tbody tr");

    if (!searchInput) return;

    searchInput.addEventListener("input", function () {

        const searchTerm = searchInput.value.trim().toLowerCase();

        tableRows.forEach(row => {

            const numberCell = row.cells[0];   // Column 1 (Number)
            const projectCell = row.cells[1];  // Column 2 (Project)

            if (!numberCell || !projectCell) return;

            const numberText = numberCell.textContent.toLowerCase();
            const projectText = projectCell.textContent.toLowerCase();

            const match =
                numberText.includes(searchTerm) ||
                projectText.includes(searchTerm);

            row.style.display = match ? "" : "none";
        });

    });

});