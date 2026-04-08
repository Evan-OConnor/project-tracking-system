document.addEventListener("DOMContentLoaded", function () {

    const projectInput = document.getElementById("projectInput");
    const projectIdInput = document.getElementById("projectId");
    const suggestions = document.getElementById("suggestions");

    if (!projectInput || !suggestions) return;

    projectInput.addEventListener("input", function () {
        let query = this.value;

        projectIdInput.value = ""; // reset if typing

        if (query.length < 2) {
            suggestions.innerHTML = "";
            return;
        }

        fetch("/cost-items/projects/search?q=" + query) // reuse endpoint
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

                    suggestions.appendChild(li);
                });
            });
    });

});