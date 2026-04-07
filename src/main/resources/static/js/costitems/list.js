document.addEventListener("DOMContentLoaded", function () {

// Live Search
    const searchInput = document.createElement("input");
    searchInput.type = "text";
    searchInput.placeholder = "Search...";
    searchInput.className = "form-control mb-3";

    const container = document.querySelector(".container");
    container.insertBefore(searchInput, container.querySelector(".table-responsive"));

    const tableRows = document.querySelectorAll("table tbody tr");

    searchInput.addEventListener("keyup", function () {

        const filter = this.value.toLowerCase();

        tableRows.forEach(row => {

            let match = false;

            row.querySelectorAll("td").forEach(td => {
                if (td.textContent.toLowerCase().includes(filter)) {
                    match = true;
                }
            });

            row.style.display = match ? "" : "none";

        });

    });

});

