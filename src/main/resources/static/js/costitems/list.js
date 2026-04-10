document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("searchInput");

    if (!searchInput) return;

    searchInput.addEventListener("input", function () {

        const filter = this.value.trim().toLowerCase();
        const tableRows = document.querySelectorAll("#expensesandoutlaysTable tbody tr");


        tableRows.forEach(row => {

            const idCell = row.cells[0];     // Column 1 (ID)
            const projectCell = row.cells[1];   // Column 2 (Project)

            if (!idCell || !projectCell) return;

            const idText = idCell.textContent.trim().toLowerCase();
            const projectText = projectCell.textContent.trim().toLowerCase();

            const match =
                idText.includes(filter) ||
                projectText.includes(filter);

            row.style.display = match ? "" : "none";
        });
    });

});