document.addEventListener("DOMContentLoaded", function () {

    console.log("JS running");

    // ---------------------------
    // Supplier toggle (existing)
    // ---------------------------
    const typeSelect = document.getElementById("typeSelect");
    const supplierField = document.getElementById("supplierField");
    const supplierSelect = document.getElementById("supplierSelect");

    function toggleSupplier() {
        const value = typeSelect.value;

        if (!value) {
            supplierField.style.display = "none";
            return;
        }

        if (value.toUpperCase() === "EXPENSE") {
            supplierField.style.display = "none";
            supplierSelect.value = "";
        } else {
            supplierField.style.display = "block";
        }
    }

    if (typeSelect && supplierField) {
        toggleSupplier();
        typeSelect.addEventListener("change", toggleSupplier);
    }

    // ---------------------------
    // Project search (NEW)
    // ---------------------------
   const projectIdInput = document.getElementById("projectId");
    const suggestions = document.getElementById("suggestions");

    if (!projectInput || !suggestions) return;

    projectInput.addEventListener("input", function () {
        let query = this.value;

        if (query.length < 2) {
            suggestions.innerHTML = "";
            return;
        }



      fetch("/cost-items/projects/search?q=" + query)
            .then(res => res.json())
            .then(data => {
                suggestions.innerHTML = "";

                data.forEach(item => {
                    let li = document.createElement("li");
                    li.className = "list-group-item list-group-item-action";
                    li.style.cursor = "pointer";
                   li.textContent = item.name;

                    li.onclick = () => {
                        projectInput.value = item.name;
                            projectIdInput.value = item.id;
                        suggestions.innerHTML = "";
                    };

                    projectInput.addEventListener("input", function () {
                        projectIdInput.value = ""; // reset ID if user edits
                    });

                    suggestions.appendChild(li);
                });
            });
    });

});