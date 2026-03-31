document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("searchInput");

    // Live search
    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = this.value.trim().toLowerCase();
            const tableRows = document.querySelectorAll("#employeesTable tbody tr");

            tableRows.forEach(row => {
                 const idCell = row.cells[0];
                const nameCell = row.cells[1]; // Name is second column (index 1)

                if (!idCell || !nameCell) return;

                const idText = idCell.textContent.trim();
                const nameText = (nameCell.textContent || "").toLowerCase();

                const match = idText.includes(filter) || nameText.includes(filter);

                row.style.display = match? "" : "none";
            });
        });
    }

});