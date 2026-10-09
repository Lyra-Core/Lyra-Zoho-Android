# Pull request descriptions

Before creating or updating a PR, read the final diff, the work item when available, and the observed verification results. Use the repository's PR template and any documented title convention.

## Write the description

- Explain the problem and the resulting user or system behavior. Link the work item when available and include enough context to understand the change without opening it. If no dedicated work item exists, state that briefly and explain the reason for the change directly.
- Keep detail proportional to the change. Describe meaningful outcomes and decisions rather than a file inventory or a transcript of the investigation.
- Verification is for evidence beyond required GitHub checks: identify the affected behavior, environment/platform, check performed, and observed result. Include an exact command when it helps reproduce that check.
- Required GitHub check results are already visible in Checks; leave them out of the body. Continue running the repository's required verification and confirming required checks before merge.
- State any relevant behavior that remains unverified and its concrete blocker. Formatting, compilation, test discovery, mocks, and checks against an unchanged deployment establish only their respective scope; describe that scope accurately.
- For visual changes, attach screenshots or a recording, or link directly to existing evidence. Caption the screen/state, platform or viewport, and before/after where useful. For accessibility changes, include relevant keyboard or screen-reader evidence when available.
- Verify that every referenced attachment or evidence link is present and accessible to reviewers. If evidence is missing, state the gap instead of claiming it is attached. Keep secrets and personal data out of evidence.
- Reviewer notes are for specific risks, design decisions, affected services/platforms, API/client impact, configuration, migrations, dependencies, rollout constraints, or follow-up work. Include rollback details when reverting the PR alone would be insufficient.
- Remove unused optional sections, placeholders, duplicate points, and routine process reminders such as "test after deployment" or "required checks must pass." Describe a deployment constraint only when it changes how this particular change must be verified or released.

## AI and author responsibility

- AI may draft the description using the sources above. Every factual claim must be supported by the diff, work item, observed output, or supplied evidence. Ask the author for missing intent or evidence that cannot be established; never invent tests, results, attachments, links, or impact claims.
- Refresh the description after implementation changes so it describes the final change. Distinguish evidence from earlier revisions when it no longer covers the final implementation.
- Before requesting review, the author reads the description, checks its accuracy, and opens the evidence links. Resolve unsupported claims and empty required content first.
- Reviewers request completion when the description is empty, misleading, or missing relevant evidence before approving the PR.

## Examples

Useful change: "Empty related-content results no longer display two placeholder tiles. Populated results keep their existing layout."

Useful verification: "Against the changed local app in Chromium, reopened an in-progress article, completed it, and confirmed it moved to Completed."

Useful gap: "The affected company's empty-content screen remains unverified because the required account was unavailable."
