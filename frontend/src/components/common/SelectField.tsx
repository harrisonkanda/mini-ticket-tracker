interface SelectOption<T extends string> {
  value: T;
  label: string;
}

interface SelectFieldProps<T extends string> {
  value: T;
  options: readonly SelectOption<T>[];
  onChange: (value: T) => void;
  /** Visible label. Omit it when the select sits in a table cell and pass `ariaLabel` instead. */
  label?: string;
  ariaLabel?: string;
  disabled?: boolean;
  className?: string;
}

/**
 * Typed wrapper around a native select: callers get the option value back as `T`
 * instead of casting `event.target.value` at every call site.
 */
export function SelectField<T extends string>({
  value,
  options,
  onChange,
  label,
  ariaLabel,
  disabled = false,
  className,
}: SelectFieldProps<T>) {
  const select = (
    <select
      value={value}
      aria-label={ariaLabel}
      disabled={disabled}
      onChange={(event) => onChange(event.target.value as T)}
    >
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.label}
        </option>
      ))}
    </select>
  );

  if (!label) {
    return select;
  }

  return (
    <label className={className}>
      {label}
      {select}
    </label>
  );
}
