document.addEventListener("DOMContentLoaded", function () {
    // Live Search
    const searchInput = document.getElementById("searchInput");
    const tableRows = document.querySelectorAll("#entriesTable tbody tr");

    if (!searchInput) return;

    searchInput.addEventListener("input", function () {

        const filter = this.value.toLowerCase();

        tableRows.forEach(row => {

            const projectCell = row.cells[0];
            const employeeCell = row.cells[1];

            if (!projectCell || !employeeCell) return;

            const text =
                projectCell.textContent.toLowerCase() +
                " " +
                employeeCell.textContent.toLowerCase();

            row.style.display = text.includes(filter) ? "" : "none";

        });

    });

});