# Electricity Billing DevOps — Actual Git Steps

This document records the Git/GitHub steps actually performed for the
`electricity-billing` Spring Boot project.

**Current branch convention for this project: `main` only.**

The old `master` branch was used during the initial Git setup and was later
renamed to `main`. From this point onward, use `main` for this project and do
not mix it with `master`.

## 1. Go to the project directory

```cmd
cd C:\Users\molak\Documents\workspace-spring-tools-for-eclipse-5.3.0.RELEASE\electricity-billing
```

This is the local Spring Boot project directory.

## 2. Initialize Git

```cmd
git init
```

This initialized a Git repository in the project directory.

## 3. Check repository status

```cmd
git status
```

This was used to see which files were untracked or modified.

## 4. Stage the project files

```cmd
git add .
```

This staged the project files for the first commit.

## 5. Create the initial commit

The project was initially committed with:

```cmd
git commit -m "Initial commit"
```

The Spring Boot project was later recorded in the commit:

```text
55e1384 Initial Spring Boot Application
```

## 6. Connect the local repository to GitHub

The GitHub repository used for the DevOps project is:

`electricity-billing-devops`

The remote was added with:

```cmd
git remote add origin <GitHub-repository-url>
```

To verify the remote:

```cmd
git remote -v
```

`origin` refers to the GitHub repository.

## 7. Initial push

During the initial setup, the project was pushed using the old branch name:

```cmd
git push -u origin master
```

This created/updated the remote branch that was then named `master`.

The project was later migrated to `main`, so `master` is no longer the branch to use.

---

# Branch cleanup: `master` → `main`

The repository initially had both `main` and `master`.

There is no technical difference between the names; they are simply branch
names. However, for this project we decided to keep **one branch only:
`main`**.

## 8. Rename the local branch

The local branch was renamed with:

```cmd
git branch -m master main
```

After the rename, `git status` showed:

```text
On branch main
Your branch is up to date with 'origin/master'.

nothing to commit, working tree clean
```

The local branch was therefore successfully renamed to `main`, but it was
temporarily still tracking the old remote branch.

## 9. First attempt to push `main`

The following command was attempted:

```cmd
git push -u origin main
```

GitHub rejected the push with:

```text
! [rejected]        main -> main (fetch first)
```

This happened because the existing remote `main` branch contained history
that was different from the local branch.

## 10. Replace the remote `main` with the project history

Because the intention was to make the current Spring Boot project the
authoritative `main` branch, the following command was used:

```cmd
git push -u origin main --force
```

The command succeeded:

```text
+ 9256726...55e1384 main -> main (forced update)
branch 'main' set up to track 'origin/main'.
```

This made the GitHub `main` branch point to commit:

```text
55e1384 Initial Spring Boot Application
```

The old remote `main` history beginning at `9256726` was replaced by the
current Spring Boot project history.

**Important:** The force push was used specifically during this branch
migration. It should not be used routinely.

---

# Current branch convention

The project now uses:

```text
main
```

as its single intended development branch.

The desired setup is:

```text
Local project
     │
     ▼
   main
     │
     ▼
origin/main
     │
     ▼
GitHub main
```

From now on, normal commands should use `main`.

For example:

```cmd
git status
git add .
git commit -m "Add feature"
git push
```

Because `main` now tracks `origin/main`, a normal:

```cmd
git push
```

is sufficient for future pushes.

---

# 11. Verify the current branch and synchronization

The following command was used:

```cmd
git status
```

After the branch rename and remote setup, the expected state is:

```text
On branch main
Your branch is up to date with 'origin/main'.
nothing to commit, working tree clean
```

This means:

- The local repository is on `main`.
- The local `main` branch tracks GitHub's `origin/main`.
- There are no uncommitted changes.

## 12. Check the commit history

The following command was used:

```cmd
git log --oneline --all --decorate
```

Before the branch cleanup, it showed:

```text
55e1384 (HEAD -> master, origin/master) Initial Spring Boot Application
```

After the rename, the equivalent current state should be:

```text
55e1384 (HEAD -> main, origin/main) Initial Spring Boot Application
```

This confirms that the local `main` and GitHub `main` point to the same commit.

---

# 13. Verify what is inside the latest commit

The following command was used:

```cmd
git show --stat --oneline HEAD
```

The latest Spring Boot commit contained 22 files, including:

- `.gitattributes`
- `.gitignore`
- Maven wrapper files
- `pom.xml`
- `ElectricityBillingApplication.java`
- `BillController.java`
- `HealthController.java`
- `UserController.java`
- `BillDao.java`
- `UserDao.java`
- `Bill.java`
- `UsageType.java`
- `User.java`
- `BillService.java`
- `BillingService.java`
- `UserService.java`
- `application.properties`
- `schema.sql`
- Spring Boot test files
- `BillingServiceTest.java`

The commit contained:

```text
22 files changed, 1135 insertions(+)
```

Therefore, the Spring Boot application source code is present in the Git
history that was moved to `main`.

---

# 14. Why GitHub initially appeared to show only `.gitignore` and `README.md`

The repository initially had both `main` and `master`, with different
histories.

The Spring Boot project was on the branch that was originally named
`master`.

During the cleanup, the Spring Boot project history was moved to the
authoritative `main` branch with:

```cmd
git push -u origin main --force
```

Therefore, the active project branch is now:

```text
main
```

---

# 15. `HEAD`, local branch, and remote branch

The notation:

```text
HEAD -> main, origin/main
```

can be understood as:

```text
HEAD
  ↓
local main
  ↓
commit 55e1384
  ↑
origin/main
  ↑
GitHub main
```

`HEAD` represents the commit currently checked out in the local repository.

`main` is the local branch.

`origin/main` is the local reference to GitHub's `main` branch.

When all point to the same commit, the local project and GitHub `main` are
synchronized.

---

# 16. `git add`, commit, and push — the basic flow

The normal workflow for this project is:

```text
Make changes
     ↓
git status
     ↓
git add .
     ↓
git commit -m "message"
     ↓
git push
     ↓
GitHub main
```

---

# 17. Commit amendment and force push

During the earlier repository setup, the initial commit needed to be
updated/replaced. The workflow used for that situation was:

```cmd
git add .
git commit --amend --no-edit
```

`git commit --amend --no-edit` replaces the most recent commit with a new
version containing the staged changes while keeping the same commit message.

Because amending a commit changes its commit ID, the remote branch may no
longer have the same history.

When remote history needs to be replaced, a force push can be used:

```cmd
git push --force origin main
```

**Important:** Force pushing rewrites remote branch history. Use it only when
you intentionally need to replace remote history and understand the effect.

---

# 18. Important interview concepts from this Git setup

### Repository

A Git repository tracks the project's files and their history.

### Commit

A commit is a recorded snapshot of project changes.

Example:

```text
55e1384 Initial Spring Boot Application
```

### Branch

A branch is a named line of development.

This project uses:

```text
main
```

### Remote

A remote is a reference to another Git repository.

In this project:

```text
origin
```

is the name of the GitHub remote.

### `origin/main`

This represents the remote-tracking branch for the `main` branch on the
`origin` repository.

### `HEAD`

`HEAD` points to the commit currently checked out in the local repository.

### `git add`

Moves changes into the staging area.

### `git commit`

Records staged changes in Git history.

### `git push`

Sends local commits to the remote repository.

### `git commit --amend`

Replaces the most recent commit with an updated commit.

### `git push --force`

Replaces remote branch history with the local branch history. It should be
used carefully because it can overwrite remote history.

---

# Current conclusion

The Electricity Billing DevOps project now follows a **single-branch
convention: `main`**.

The intended setup is:

```text
Local project
     │
     │ main
     ▼
commit 55e1384
     │
     ▼
origin/main
     │
     ▼
GitHub main
```

**From this point forward, use `main` everywhere for this project.**

Do not use `master` in future project instructions unless specifically
discussing the historical branch rename described above.
