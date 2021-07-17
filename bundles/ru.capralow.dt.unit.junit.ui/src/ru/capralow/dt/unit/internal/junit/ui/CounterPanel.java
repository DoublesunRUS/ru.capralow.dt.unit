package ru.capralow.dt.unit.internal.junit.ui;

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ru.capralow.dt.unit.internal.junit.ui.util.SWTUtil;

public class CounterPanel
    extends Composite
{
    protected Text fNumberOfErrors;
    protected Text fNumberOfFailures;
    protected Text fNumberOfRuns;
    protected int fTotal;
    protected int fIgnoredCount;
    protected int fAssumptionFailedCount;
    private final Image fErrorIcon = JUnitUiPlugin.createImage("ovr16/error_ovr.png"); //$NON-NLS-1$
    private final Image fFailureIcon = JUnitUiPlugin.createImage("ovr16/failed_ovr.png"); //$NON-NLS-1$

    public CounterPanel(Composite parent)
    {
        super(parent, 64);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 9;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);
        this.fNumberOfRuns = this.createLabel(Messages.CounterPanel_label_runs, null, " 0/0  "); //$NON-NLS-1$
        this.fNumberOfErrors = this.createLabel(Messages.CounterPanel_label_errors, this.fErrorIcon, " 0 "); //$NON-NLS-1$
        this.fNumberOfFailures = this.createLabel(Messages.CounterPanel_label_failures, this.fFailureIcon, " 0 "); //$NON-NLS-1$
        this.addDisposeListener(e -> this.disposeIcons());
    }

    public int getTotal()
    {
        return this.fTotal;
    }

    public void reset()
    {
        this.setErrorValue(0);
        this.setFailureValue(0);
        this.setRunValue(0, 0, 0);
        this.fTotal = 0;
    }

    public void setErrorValue(int value)
    {
        this.fNumberOfErrors.setText(Integer.toString(value));
        this.redraw();
    }

    public void setFailureValue(int value)
    {
        this.fNumberOfFailures.setText(Integer.toString(value));
        this.redraw();
    }

    public void setRunValue(int value, int ignoredCount, int assumptionFailureCount)
    {
        String runStringTooltip;
        String runString;
        if (ignoredCount == 0 && assumptionFailureCount == 0)
        {
            runStringTooltip = runString = MessageFormat.format(Messages.CounterPanel_runcount,
                (Object[])new String[] { Integer.toString(value), Integer.toString(this.fTotal) });
        }
        else if (ignoredCount != 0 && assumptionFailureCount == 0)
        {
            runString = MessageFormat.format(Messages.CounterPanel_runcount_skipped, (Object[])new String[] {
                Integer.toString(value), Integer.toString(this.fTotal), Integer.toString(ignoredCount) });
            runStringTooltip = MessageFormat.format(Messages.CounterPanel_runcount_ignored, (Object[])new String[] {
                Integer.toString(value), Integer.toString(this.fTotal), Integer.toString(ignoredCount) });
        }
        else if (ignoredCount == 0 && assumptionFailureCount != 0)
        {
            runString = MessageFormat.format(Messages.CounterPanel_runcount_skipped, (Object[])new String[] {
                Integer.toString(value), Integer.toString(this.fTotal), Integer.toString(assumptionFailureCount) });
            runStringTooltip =
                MessageFormat.format(Messages.CounterPanel_runcount_assumptionsFailed, (Object[])new String[] {
                    Integer.toString(value), Integer.toString(this.fTotal), Integer.toString(assumptionFailureCount) });
        }
        else
        {
            runString = MessageFormat.format(Messages.CounterPanel_runcount_skipped,
                (Object[])new String[] { Integer.toString(value), Integer.toString(this.fTotal),
                    Integer.toString(ignoredCount + assumptionFailureCount) });
            runStringTooltip = MessageFormat.format(Messages.CounterPanel_runcount_ignored_assumptionsFailed,
                (Object[])new String[] { Integer.toString(value), Integer.toString(this.fTotal),
                    Integer.toString(ignoredCount), Integer.toString(assumptionFailureCount) });
        }
        this.fNumberOfRuns.setText(runString);
        this.fNumberOfRuns.setToolTipText(runStringTooltip);
        if (this.fIgnoredCount == 0 && ignoredCount > 0 || this.fIgnoredCount != 0 && ignoredCount == 0)
        {
            this.layout();
        }
        else if (this.fAssumptionFailedCount == 0 && assumptionFailureCount > 0
            || this.fAssumptionFailedCount != 0 && assumptionFailureCount == 0)
        {
            this.layout();
        }
        else
        {
            this.fNumberOfRuns.redraw();
            this.redraw();
        }
        this.fIgnoredCount = ignoredCount;
        this.fAssumptionFailedCount = assumptionFailureCount;
    }

    public void setTotal(int value)
    {
        this.fTotal = value;
    }

    private Text createLabel(String name, Image image, String init)
    {
        Label label = new Label(this, 0);
        if (image != null)
        {
            image.setBackground(label.getBackground());
            label.setImage(image);
        }
        label.setLayoutData(new GridData(32));
        label = new Label(this, 0);
        label.setText(name);
        label.setLayoutData(new GridData(32));
        Text value = new Text(this, 8);
        value.setText(init);
        SWTUtil.fixReadonlyTextBackground(value);
        value.setLayoutData(new GridData(800));
        return value;
    }

    private void disposeIcons()
    {
        this.fErrorIcon.dispose();
        this.fFailureIcon.dispose();
    }
}
