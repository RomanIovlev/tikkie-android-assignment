class TestReporter {
    
    /**
     * Parses duration string to milliseconds
     */
    static int parseDurationToMillis(String durationString) {
        if (!durationString) return 0
        
        int totalMillis = 0
        def millisMatch = durationString =~ /(\d+)\s*millis?/
        def secondsMatch = durationString =~ /(\d+)\s*seconds?/
        def minutesMatch = durationString =~ /(\d+)\s*minutes?/
        
        if (millisMatch) {
            totalMillis += Integer.parseInt(millisMatch[0][1])
        }
        if (secondsMatch) {
            totalMillis += Integer.parseInt(secondsMatch[0][1]) * 1000
        }
        if (minutesMatch) {
            totalMillis += Integer.parseInt(minutesMatch[0][1]) * 60000
        }
        
        return totalMillis
    }
    
    /**
     * Formats duration according to requirements
     */
    static String formatDuration(int millis) {
        if (millis <= 0) return ''
        
        int totalMillis = millis
        
        if (totalMillis >= 60000) {
            // >= 1 minute: "X min Y sec"
            int minutes = totalMillis / 60000
            int seconds = (totalMillis % 60000) / 1000
            return "${minutes} min ${seconds} sec"
        } else if (totalMillis >= 1000) {
            // >= 1 second but < 1 minute: "X sec"
            int seconds = totalMillis / 1000
            return "${seconds} sec"
        } else {
            // < 1 second: "X ms"
            return "${totalMillis} ms"
        }
    }
    
    /**
     * Generates unified HTML for all tests in a class
     */
    static String generateUnifiedStepsHtml(List tests, String containerId = 'kaspresso-steps-unified', String stepIdPrefix = 'test') {
        def html = """
<div id="${containerId}" style="margin: 20px 0; padding: 20px; background-color: #f9f9f9; border-radius: 5px;">
<h2 style="color: #333; margin-bottom: 15px;">Tests</h2>
<style>
.kaspresso-step-row {
    cursor: pointer;
    user-select: none;
}
.kaspresso-step-row:hover {
    background-color: #f0f0f0;
}
.kaspresso-step-content {
    display: none;
}
.kaspresso-step-content.expanded {
    display: table-row !important;
}
.kaspresso-toggle {
    display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    margin-right: 8px;
    font-weight: bold;
    color: #666;
}
.kaspresso-toggle.expanded::before {
    content: '▼';
}
.kaspresso-toggle.collapsed::before {
    content: '▶';
}
.kaspresso-step-indent {
    display: inline-block;
    width: 20px;
}
.kaspresso-nested-row {
    background-color: #fafafa;
}
.kaspresso-test-row {
    background-color: #e8f5e9;
    font-weight: bold;
}
</style>
<table style="width: 100%; border-collapse: collapse; background-color: white;">
<thead>
<tr style="background-color: #4CAF50; color: white;">
<th style="padding: 12px; text-align: left; border: 1px solid #ddd; width: 60%;">Test / Step</th>
<th style="padding: 12px; text-align: left; border: 1px solid #ddd; width: 10%;">Status</th>
<th style="padding: 12px; text-align: left; border: 1px solid #ddd; width: 30%;">Duration</th>
</tr>
</thead>
<tbody id="${containerId}-body">
"""
        
        def stepCounter = [value: 0]
        html += generateStepRows(tests, stepIdPrefix, stepCounter, "", null, containerId, 'toggleStep')
        
        html += """
</tbody>
</table>
</div>
<script>
(function(containerId) {
    var container = document.getElementById(containerId);
    if (!container) return;
    
    function toggleStep(stepId) {
        var toggle = container.querySelector('#toggle-' + stepId);
        if (!toggle) return;
        
        var isExpanded = toggle.classList.contains('expanded');
        var childRows = container.querySelectorAll('[data-parent="' + stepId + '"]');
        
        if (isExpanded) {
            childRows.forEach(function(row) {
                row.style.display = 'none';
                collapseAllDescendants(row, container);
            });
            toggle.classList.remove('expanded');
            toggle.classList.add('collapsed');
        } else {
            childRows.forEach(function(row) {
                row.style.display = 'table-row';
            });
            toggle.classList.remove('collapsed');
            toggle.classList.add('expanded');
        }
    }
    
    function collapseAllDescendants(parentRow, container) {
        var toggle = parentRow.querySelector('[id^="toggle-"]');
        if (toggle) {
            var nestedStepId = toggle.id.replace('toggle-', '');
            var nestedChildren = container.querySelectorAll('[data-parent="' + nestedStepId + '"]');
            nestedChildren.forEach(function(nested) {
                nested.style.display = 'none';
                collapseAllDescendants(nested, container);
            });
            toggle.classList.remove('expanded');
            toggle.classList.add('collapsed');
        }
    }
    
    // Use event delegation on the container
    container.addEventListener('click', function(e) {
        var row = e.target.closest('[data-step-id]');
        if (row) {
            var stepId = row.getAttribute('data-step-id');
            if (stepId) {
                toggleStep(stepId);
            }
        }
    });
    
    // Function to toggle screenshot visibility
    window.toggleScreenshot = function(screenshotId) {
        var toggle = document.getElementById('toggle-' + screenshotId);
        var content = document.getElementById('screenshot-content-' + screenshotId);
        if (!toggle || !content) return;
        
        var isExpanded = toggle.classList.contains('expanded');
        if (isExpanded) {
            content.style.display = 'none';
            toggle.classList.remove('expanded');
            toggle.classList.add('collapsed');
        } else {
            content.style.display = 'table-row';
            toggle.classList.remove('collapsed');
            toggle.classList.add('expanded');
        }
    };
})('${containerId}');
</script>
"""
        return html
    }
    
    /**
     * Generates HTML for steps (kept for backward compatibility)
     */
    static String generateStepsHtml(List steps, String containerId = 'kaspresso-steps', String jsFunctionName = 'toggleStep', String stepIdPrefix = 'step') {
        def html = """
<div id="${containerId}" style="margin: 20px 0; padding: 20px; background-color: #f9f9f9; border-radius: 5px;">
<h2 style="color: #333; margin-bottom: 15px;">Kaspresso Test Steps</h2>
<style>
.kaspresso-step-row {
    cursor: pointer;
    user-select: none;
}
.kaspresso-step-row:hover {
    background-color: #f0f0f0;
}
.kaspresso-step-content {
    display: none;
}
.kaspresso-step-content.expanded {
    display: table-row !important;
}
.kaspresso-toggle {
    display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    margin-right: 8px;
    font-weight: bold;
    color: #666;
}
.kaspresso-toggle.expanded::before {
    content: '▼';
}
.kaspresso-toggle.collapsed::before {
    content: '▶';
}
.kaspresso-step-indent {
    display: inline-block;
    width: 20px;
}
.kaspresso-nested-row {
    background-color: #fafafa;
}
</style>
<table style="width: 100%; border-collapse: collapse; background-color: white;">
<thead>
<tr style="background-color: #4CAF50; color: white;">
<th style="padding: 12px; text-align: left; border: 1px solid #ddd; width: 60%;">Step</th>
<th style="padding: 12px; text-align: left; border: 1px solid #ddd; width: 10%;">Status</th>
<th style="padding: 12px; text-align: left; border: 1px solid #ddd; width: 30%;">Duration</th>
</tr>
</thead>
<tbody id="${containerId}-body">
"""
        
        def stepCounter = [value: 0]
        html += generateStepRows(steps, stepIdPrefix, stepCounter, "", null, containerId)
        
        html += """
</tbody>
</table>
</div>
<script>
(function(containerId) {
    var container = document.getElementById(containerId);
    if (!container) return;
    
    function toggleStep(stepId) {
        var toggle = container.querySelector('#toggle-' + stepId);
        if (!toggle) return;
        
        var isExpanded = toggle.classList.contains('expanded');
        var childRows = container.querySelectorAll('[data-parent="' + stepId + '"]');
        
        if (isExpanded) {
            childRows.forEach(function(row) {
                row.style.display = 'none';
                collapseAllDescendants(row, container);
            });
            toggle.classList.remove('expanded');
            toggle.classList.add('collapsed');
        } else {
            childRows.forEach(function(row) {
                row.style.display = 'table-row';
            });
            toggle.classList.remove('collapsed');
            toggle.classList.add('expanded');
        }
    }
    
    function collapseAllDescendants(parentRow, container) {
        var toggle = parentRow.querySelector('[id^="toggle-"]');
        if (toggle) {
            var nestedStepId = toggle.id.replace('toggle-', '');
            var nestedChildren = container.querySelectorAll('[data-parent="' + nestedStepId + '"]');
            nestedChildren.forEach(function(nested) {
                nested.style.display = 'none';
                collapseAllDescendants(nested, container);
            });
            toggle.classList.remove('expanded');
            toggle.classList.add('collapsed');
        }
    }
    
    // Use event delegation on the container
    container.addEventListener('click', function(e) {
        var row = e.target.closest('[data-step-id]');
        if (row) {
            var stepId = row.getAttribute('data-step-id');
            if (stepId) {
                toggleStep(stepId);
            }
        }
    });
    
    // Function to toggle screenshot visibility
    window.toggleScreenshot = function(screenshotId) {
        var toggle = document.getElementById('toggle-' + screenshotId);
        var content = document.getElementById('screenshot-content-' + screenshotId);
        if (!toggle || !content) return;
        
        var isExpanded = toggle.classList.contains('expanded');
        if (isExpanded) {
            content.style.display = 'none';
            toggle.classList.remove('expanded');
            toggle.classList.add('collapsed');
        } else {
            content.style.display = 'table-row';
            toggle.classList.remove('collapsed');
            toggle.classList.add('expanded');
        }
    };
})('${containerId}');
</script>
"""
        return html
    }
    
    /**
     * Generates step rows recursively
     */
    static String generateStepRows(List steps, String stepIdPrefix, Map stepCounter, String prefix = "", String parentId = null, String containerId = 'kaspresso-steps', String jsFunctionName = 'toggleStep') {
        def html = ''
        def currentIdx = 1
        steps.each { step ->
            def stepId = "${stepIdPrefix}-${stepCounter.value++}"
            def statusColor = step.status == 'SUCCEED' ? '#4CAF50' : (step.status == 'FAILED' ? '#f44336' : '#9e9e9e')
            def statusText = step.status == 'SUCCEED' ? 'PASSED' : (step.status == 'FAILED' ? 'FAILED' : 'RUNNING')
            def hasChildren = step.children && step.children.size() > 0
            def isNested = parentId != null
            
            def currentPrefix = prefix == "" ? "${currentIdx}." : "${prefix}${currentIdx}."
            def indentLevel = prefix.split('\\\\.').length - 1
            if (prefix == "") indentLevel = 0
            def indent = indentLevel * 20
            
            // Format duration
            def formattedDuration = ''
            if (step.duration) {
                // Check if duration is already in the new format (e.g., "2 min 23 sec", "53 sec", "423 ms")
                // The new format doesn't contain "minutes", "seconds", or "millis" (plural forms)
                def isNewFormat = step.duration =~ /^\d+\s+(min|sec|ms)$/ || 
                                   step.duration =~ /^\d+\s+min\s+\d+\s+sec$/
                
                if (isNewFormat) {
                    // Already formatted in new format, use as is
                    formattedDuration = step.duration
                } else {
                    // Need to parse and format (handles verbose format like "0 minutes, 0 seconds and 968 millis")
                    int millis = parseDurationToMillis(step.duration)
                    formattedDuration = formatDuration(millis)
                }
            }
            
            def hasScreenshot = step.screenshot && step.screenshot != ''
            def effectiveHasChildren = hasChildren || hasScreenshot
            
            // Main step row - use container-scoped toggle function with event delegation
            def rowStyle = isNested ? "display: none; border-bottom: 1px solid #ddd;" : "border-bottom: 1px solid #ddd;"
            def cursorStyle = effectiveHasChildren ? "cursor: pointer; " : ""
            def dataStepId = effectiveHasChildren ? "data-step-id=\"${stepId}\"" : ""
            // Check if this is a top-level test (no parent and has children) - make it stand out
            def isTestRow = (parentId == null && effectiveHasChildren) ? " kaspresso-test-row" : ""
            html += """
<tr class="kaspresso-step-row${isNested ? ' kaspresso-nested-row kaspresso-step-content' : ''}${isTestRow}" ${dataStepId} ${isNested ? "data-parent=\"${parentId}\"" : ''} style="${cursorStyle}${rowStyle}">
<td style="padding: 10px; border: 1px solid #ddd;">
    <span style="margin-left: ${indent}px;">
        ${effectiveHasChildren ? "<span class=\"kaspresso-toggle collapsed\" id=\"toggle-${stepId}\"></span>" : "<span class=\"kaspresso-step-indent\"></span>"}
        ${currentPrefix} ${step.name}
    </span>
</td>
<td style="padding: 10px; border: 1px solid #ddd; color: ${statusColor}; font-weight: bold;">${statusText}</td>
<td style="padding: 10px; border: 1px solid #ddd;">${formattedDuration}</td>
</tr>
"""
            
            if (hasChildren) {
                html += generateStepRows(step.children, stepIdPrefix, stepCounter, currentPrefix, stepId, containerId, jsFunctionName)
            }
            
            // Add screenshot as a collapsed block if screenshot exists
            if (hasScreenshot) {
                def screenshotIndent = indentLevel * 20
                def screenshotStepId = "${stepIdPrefix}-screenshot-${stepCounter.value++}"
                // step.screenshot should now contain just the filename after copyScreenshots runs
                def screenshotFileName = step.screenshot
                def screenshotRelativePath = "screenshots/${screenshotFileName}"
                
                // Screenshot block - collapsed by default, clickable to expand
                html += """
<tr class="kaspresso-step-row kaspresso-nested-row kaspresso-step-content" data-parent="${stepId}" data-step-id="${screenshotStepId}" style="display: none; border-bottom: 1px solid #ddd; cursor: pointer;" onclick="toggleScreenshot('${screenshotStepId}')">
</tr>
<tr class="kaspresso-nested-row kaspresso-step-content" data-parent="${stepId}" id="screenshot-content-${screenshotStepId}" style="display: none; border-bottom: 1px solid #ddd;">
<td colspan="3" style="padding: 10px; border: 1px solid #ddd; background-color: #fafafa;">
    <div style="margin-left: ${screenshotIndent + 20}px; max-height: 500px; overflow: auto;">
        <img src="${screenshotRelativePath}" alt="Screenshot for ${step.name}" style="max-width: 100%; max-height: 500px; height: auto; border: 1px solid #ddd; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);" />
        <div style="margin-top: 5px; font-size: 12px; color: #666;">
            <a href="${screenshotRelativePath}" target="_blank" style="color: #2196F3; text-decoration: none;">Open full size</a>
        </div>
    </div>
</td>
</tr>
"""
            }
            
            currentIdx++
        }
        return html
    }
}
