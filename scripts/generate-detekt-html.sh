#!/bin/bash

# Generate HTML report from merged Detekt XML report
# Usage: ./scripts/generate-detekt-html.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
XML_REPORT="$PROJECT_ROOT/build/reports/detekt/merge.xml"
HTML_REPORT="$PROJECT_ROOT/build/reports/detekt/merge.html"

if [ ! -f "$XML_REPORT" ]; then
    echo "❌ Error: Merged XML report not found at $XML_REPORT"
    echo "   Run './gradlew detektAll --continue' first"
    exit 1
fi

echo "📊 Generating HTML report from merged XML..."

# Generate HTML report using XSLT transformation
cat > /tmp/detekt-html-transform.xsl << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/checkstyle">
        <html>
        <head>
            <title>Detekt Report - Merged</title>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background: #f5f5f5; padding: 20px; }
                .container { max-width: 1400px; margin: 0 auto; }
                .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                .header h1 { font-size: 32px; margin-bottom: 10px; }
                .header .subtitle { opacity: 0.9; font-size: 16px; }
                .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
                .stat-card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                .stat-card .number { font-size: 36px; font-weight: bold; color: #667eea; margin-bottom: 5px; }
                .stat-card .label { color: #666; font-size: 14px; text-transform: uppercase; letter-spacing: 0.5px; }
                .file-section { background: white; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden; }
                .file-header { background: #f8f9fa; padding: 15px 20px; border-bottom: 2px solid #e9ecef; cursor: pointer; display: flex; justify-content: space-between; align-items: center; }
                .file-header:hover { background: #e9ecef; }
                .file-path { font-family: "SF Mono", Monaco, monospace; font-size: 14px; color: #333; font-weight: 500; }
                .error-count { background: #dc3545; color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: bold; }
                .errors { padding: 20px; display: none; }
                .errors.active { display: block; }
                .error-item { padding: 15px; margin-bottom: 10px; border-left: 4px solid #dc3545; background: #fff5f5; border-radius: 4px; }
                .error-location { font-family: "SF Mono", Monaco, monospace; font-size: 13px; color: #666; margin-bottom: 8px; }
                .error-message { color: #333; line-height: 1.6; }
                .error-rule { display: inline-block; background: #667eea; color: white; padding: 2px 8px; border-radius: 3px; font-size: 11px; margin-top: 8px; font-family: "SF Mono", Monaco, monospace; }
                .module-badge { display: inline-block; background: #28a745; color: white; padding: 2px 8px; border-radius: 3px; font-size: 11px; margin-left: 10px; }
                .no-issues { text-align: center; padding: 60px 20px; color: #666; }
                .no-issues .icon { font-size: 48px; margin-bottom: 20px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🔍 Detekt Analysis Report</h1>
                    <div class="subtitle">Merged report from all modules</div>
                </div>

                <div class="stats">
                    <div class="stat-card">
                        <div class="number"><xsl:value-of select="count(file)"/></div>
                        <div class="label">Files with Issues</div>
                    </div>
                    <div class="stat-card">
                        <div class="number"><xsl:value-of select="count(file/error)"/></div>
                        <div class="label">Total Violations</div>
                    </div>
                    <div class="stat-card">
                        <div class="number"><xsl:value-of select="count(file/error[@severity='error'])"/></div>
                        <div class="label">Errors</div>
                    </div>
                    <div class="stat-card">
                        <div class="number"><xsl:value-of select="count(file/error[@severity='warning'])"/></div>
                        <div class="label">Warnings</div>
                    </div>
                </div>

                <xsl:choose>
                    <xsl:when test="count(file) = 0">
                        <div class="no-issues">
                            <div class="icon">✅</div>
                            <h2>No Issues Found!</h2>
                            <p>All code quality checks passed successfully.</p>
                        </div>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="file"/>
                    </xsl:otherwise>
                </xsl:choose>
            </div>

            <script>
                document.querySelectorAll('.file-header').forEach(header => {
                    header.addEventListener('click', () => {
                        const errors = header.nextElementSibling;
                        errors.classList.toggle('active');
                    });
                });
                // Expand first file by default
                if (document.querySelector('.errors')) {
                    document.querySelector('.errors').classList.add('active');
                }
            </script>
        </body>
        </html>
    </xsl:template>

    <xsl:template match="file">
        <div class="file-section">
            <div class="file-header">
                <div>
                    <span class="file-path"><xsl:value-of select="@name"/></span>
                    <xsl:variable name="module" select="substring-before(substring-after(@name, '/'), '/')"/>
                    <xsl:if test="$module != ''">
                        <span class="module-badge"><xsl:value-of select="$module"/></span>
                    </xsl:if>
                </div>
                <span class="error-count"><xsl:value-of select="count(error)"/> issues</span>
            </div>
            <div class="errors">
                <xsl:apply-templates select="error"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="error">
        <div class="error-item">
            <div class="error-location">
                Line <xsl:value-of select="@line"/>, Column <xsl:value-of select="@column"/>
            </div>
            <div class="error-message"><xsl:value-of select="@message"/></div>
            <span class="error-rule"><xsl:value-of select="@source"/></span>
        </div>
    </xsl:template>
</xsl:stylesheet>
EOF

# Transform XML to HTML using xsltproc
if command -v xsltproc &> /dev/null; then
    xsltproc -o "$HTML_REPORT" /tmp/detekt-html-transform.xsl "$XML_REPORT"
    echo "✅ HTML report generated successfully!"
    echo "📄 Report location: $HTML_REPORT"
    echo ""
    echo "💡 Open in browser:"
    echo "   open $HTML_REPORT"
else
    echo "⚠️  xsltproc not found, using Python fallback..."

    # Python fallback for HTML generation
    python3 << PYTHON
import xml.etree.ElementTree as ET
from pathlib import Path

xml_file = Path("$XML_REPORT")
html_file = Path("$HTML_REPORT")

tree = ET.parse(xml_file)
root = tree.getroot()

files = root.findall('file')
total_errors = sum(len(f.findall('error')) for f in files)

html = f'''<!DOCTYPE html>
<html>
<head>
    <title>Detekt Report - Merged</title>
    <style>
        * {{ margin: 0; padding: 0; box-sizing: border-box; }}
        body {{ font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background: #f5f5f5; padding: 20px; }}
        .container {{ max-width: 1400px; margin: 0 auto; }}
        .header {{ background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }}
        .header h1 {{ font-size: 32px; margin-bottom: 10px; }}
        .stats {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }}
        .stat-card {{ background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }}
        .stat-card .number {{ font-size: 36px; font-weight: bold; color: #667eea; margin-bottom: 5px; }}
        .stat-card .label {{ color: #666; font-size: 14px; text-transform: uppercase; }}
        .file-section {{ background: white; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden; }}
        .file-header {{ background: #f8f9fa; padding: 15px 20px; border-bottom: 2px solid #e9ecef; cursor: pointer; }}
        .file-header:hover {{ background: #e9ecef; }}
        .file-path {{ font-family: "SF Mono", Monaco, monospace; font-size: 14px; color: #333; font-weight: 500; }}
        .error-count {{ background: #dc3545; color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: bold; }}
        .errors {{ padding: 20px; display: none; }}
        .errors.active {{ display: block; }}
        .error-item {{ padding: 15px; margin-bottom: 10px; border-left: 4px solid #dc3545; background: #fff5f5; border-radius: 4px; }}
        .error-location {{ font-family: "SF Mono", Monaco, monospace; font-size: 13px; color: #666; margin-bottom: 8px; }}
        .error-message {{ color: #333; line-height: 1.6; }}
        .error-rule {{ display: inline-block; background: #667eea; color: white; padding: 2px 8px; border-radius: 3px; font-size: 11px; margin-top: 8px; }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔍 Detekt Analysis Report</h1>
            <div>Merged report from all modules</div>
        </div>

        <div class="stats">
            <div class="stat-card">
                <div class="number">{len(files)}</div>
                <div class="label">Files with Issues</div>
            </div>
            <div class="stat-card">
                <div class="number">{total_errors}</div>
                <div class="label">Total Violations</div>
            </div>
        </div>
'''

if len(files) == 0:
    html += '''
        <div style="text-align: center; padding: 60px;">
            <h2>✅ No Issues Found!</h2>
        </div>
    '''
else:
    for file_elem in files:
        file_path = file_elem.get('name')
        errors = file_elem.findall('error')
        module = file_path.split('/')[6] if len(file_path.split('/')) > 6 else ''

        html += f'''
        <div class="file-section">
            <div class="file-header" onclick="this.nextElementSibling.classList.toggle('active')">
                <span class="file-path">{file_path}</span>
                <span class="error-count">{len(errors)} issues</span>
            </div>
            <div class="errors">
        '''

        for error in errors:
            line = error.get('line', '?')
            col = error.get('column', '?')
            message = error.get('message', '')
            source = error.get('source', '')

            html += f'''
                <div class="error-item">
                    <div class="error-location">Line {line}, Column {col}</div>
                    <div class="error-message">{message}</div>
                    <span class="error-rule">{source}</span>
                </div>
            '''

        html += '''
            </div>
        </div>
        '''

html += '''
    </div>
    <script>
        // Expand first file
        if (document.querySelector('.errors')) {
            document.querySelector('.errors').classList.add('active');
        }
    </script>
</body>
</html>
'''

html_file.write_text(html)
print(f"✅ HTML report generated successfully!")
print(f"📄 Report location: {html_file}")
PYTHON
fi

# Optionally open in browser (macOS) - only if OPEN_BROWSER env var is set
if [[ "$OSTYPE" == "darwin"* ]] && [[ "$OPEN_BROWSER" == "true" ]]; then
    echo ""
    echo "Opening report in browser..."
    open "$HTML_REPORT"
fi
