const API_BASE = "/api/resumes";

const form = document.getElementById("resume-form");
const statusText = document.getElementById("form-status");
const listContainer = document.getElementById("resume-list");
const educationsContainer = document.getElementById("educations");
const experiencesContainer = document.getElementById("experiences");

const eduTemplate = document.getElementById("education-template");
const expTemplate = document.getElementById("experience-template");

let editingId = null;

function createItem(container, template, values = {}) {
    const node = template.content.cloneNode(true);
    const card = node.querySelector(".item-card");

    card.querySelectorAll("[data-field]").forEach((input) => {
        const key = input.dataset.field;
        input.value = values[key] || "";
        input.addEventListener("input", updatePreview);
    });

    card.querySelector(".remove-item").addEventListener("click", () => {
        card.remove();
        updatePreview();
    });

    container.appendChild(node);
}

function collectItems(container) {
    return Array.from(container.querySelectorAll(".item-card"))
        .map((card) => {
            const obj = {};
            card.querySelectorAll("[data-field]").forEach((input) => {
                obj[input.dataset.field] = input.value.trim();
            });
            return obj;
        })
        .filter((entry) => Object.values(entry).some(Boolean));
}

function formToPayload() {
    const skills = document.getElementById("skills").value
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean);

    return {
        fullName: document.getElementById("fullName").value.trim(),
        headline: document.getElementById("headline").value.trim(),
        email: document.getElementById("email").value.trim(),
        phone: document.getElementById("phone").value.trim(),
        summary: document.getElementById("summary").value.trim(),
        skills,
        educations: collectItems(educationsContainer),
        experiences: collectItems(experiencesContainer)
    };
}

function setStatus(message, isError = false) {
    statusText.textContent = message;
    statusText.style.color = isError ? "#b42318" : "#0c5953";
}

function clearDynamicSections() {
    educationsContainer.innerHTML = "";
    experiencesContainer.innerHTML = "";
}

function resetForm() {
    editingId = null;
    form.reset();
    clearDynamicSections();
    createItem(educationsContainer, eduTemplate);
    createItem(experiencesContainer, expTemplate);
    document.getElementById("save-btn").textContent = "Save Resume";
    setStatus("Ready");
    updatePreview();
}

function fillForm(resume) {
    editingId = resume.id;
    document.getElementById("fullName").value = resume.fullName || "";
    document.getElementById("headline").value = resume.headline || "";
    document.getElementById("email").value = resume.email || "";
    document.getElementById("phone").value = resume.phone || "";
    document.getElementById("summary").value = resume.summary || "";
    document.getElementById("skills").value = (resume.skills || []).join(", ");

    clearDynamicSections();
    (resume.educations || []).forEach((edu) => createItem(educationsContainer, eduTemplate, edu));
    (resume.experiences || []).forEach((exp) => createItem(experiencesContainer, expTemplate, exp));
    if (!resume.educations?.length) createItem(educationsContainer, eduTemplate);
    if (!resume.experiences?.length) createItem(experiencesContainer, expTemplate);

    document.getElementById("save-btn").textContent = "Update Resume";
    setStatus(`Editing: ${resume.fullName}`);
    updatePreview();
}

async function deleteResume(id) {
    const response = await fetch(`${API_BASE}/${id}`, { method: "DELETE" });
    if (!response.ok) {
        throw new Error("Failed to delete resume.");
    }
}

function renderResumeList(resumes) {
    listContainer.innerHTML = "";
    if (!resumes.length) {
        listContainer.innerHTML = "<p class='muted'>No resumes yet.</p>";
        return;
    }

    resumes.forEach((resume) => {
        const wrapper = document.createElement("div");
        wrapper.className = "resume-item";
        const updated = new Date(resume.updatedAt).toLocaleString();
        wrapper.innerHTML = `
            <strong>${resume.fullName}</strong>
            <small>${resume.headline}</small><br>
            <small>Updated: ${updated}</small>
            <div class="item-actions">
                <button type="button" class="ghost">Edit</button>
                <button type="button" class="danger">Delete</button>
            </div>
        `;

        const [editBtn, deleteBtn] = wrapper.querySelectorAll("button");
        editBtn.addEventListener("click", () => fillForm(resume));
        deleteBtn.addEventListener("click", async () => {
            try {
                await deleteResume(resume.id);
                if (editingId === resume.id) {
                    resetForm();
                }
                await loadResumes();
                setStatus("Resume deleted.");
            } catch (error) {
                setStatus(error.message, true);
            }
        });

        listContainer.appendChild(wrapper);
    });
}

async function loadResumes() {
    const response = await fetch(API_BASE);
    if (!response.ok) {
        throw new Error("Failed to load resumes.");
    }
    const resumes = await response.json();
    renderResumeList(resumes);
}

function updatePreview() {
    const payload = formToPayload();
    document.getElementById("p-name").textContent = payload.fullName || "Your Name";
    document.getElementById("p-headline").textContent = payload.headline || "Your headline";
    const contact = [payload.email || "you@example.com", payload.phone || "+1 000 000 0000"]
        .filter(Boolean)
        .join(" | ");
    document.getElementById("p-contact").textContent = contact;
    document.getElementById("p-summary").textContent = payload.summary || "Your summary will appear here.";
    document.getElementById("p-skills").textContent = payload.skills.length
        ? payload.skills.join(" | ")
        : "No skills added yet.";

    const eduEl = document.getElementById("p-education");
    if (!payload.educations.length) {
        eduEl.textContent = "No education added yet.";
    } else {
        eduEl.innerHTML = payload.educations.map((e) => `
            <div><strong>${e.degree || ""}</strong> - ${e.school || ""}<br>${[e.startDate, e.endDate].filter(Boolean).join(" to ")}<br>${e.description || ""}</div>
        `).join("");
    }

    const expEl = document.getElementById("p-experience");
    if (!payload.experiences.length) {
        expEl.textContent = "No experience added yet.";
    } else {
        expEl.innerHTML = payload.experiences.map((e) => `
            <div><strong>${e.role || ""}</strong> - ${e.company || ""}<br>${[e.startDate, e.endDate].filter(Boolean).join(" to ")}<br>${e.description || ""}</div>
        `).join("");
    }
}

async function submitForm(event) {
    event.preventDefault();
    const payload = formToPayload();
    const method = editingId ? "PUT" : "POST";
    const url = editingId ? `${API_BASE}/${editingId}` : API_BASE;

    try {
        const response = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const body = await response.json().catch(() => ({}));
            const message = body.detail || "Failed to save resume.";
            throw new Error(message);
        }

        const saved = await response.json();
        editingId = saved.id;
        document.getElementById("save-btn").textContent = "Update Resume";
        await loadResumes();
        setStatus("Resume saved successfully.");
    } catch (error) {
        setStatus(error.message, true);
    }
}

document.getElementById("add-education").addEventListener("click", () => {
    createItem(educationsContainer, eduTemplate);
    updatePreview();
});

document.getElementById("add-experience").addEventListener("click", () => {
    createItem(experiencesContainer, expTemplate);
    updatePreview();
});

document.getElementById("reset-btn").addEventListener("click", resetForm);
form.addEventListener("submit", submitForm);
form.addEventListener("input", updatePreview);

resetForm();
loadResumes().catch((error) => setStatus(error.message, true));
