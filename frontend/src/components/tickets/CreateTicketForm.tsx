import { useState } from 'react';
import type { FormEvent } from 'react';
import {
  DEFAULT_PRIORITY,
  DESCRIPTION_MAX_LENGTH,
  TITLE_MAX_LENGTH,
} from '../../constants/ticket';
import type { CreateTicketPayload, TicketPriority } from '../../types/ticket';
import { toErrorMessage } from '../../utils/errors';
import { SelectField } from '../common/SelectField';
import { PRIORITY_OPTIONS } from './ticketOptions';

interface CreateTicketFormProps {
  /** Persists the ticket. The form shows the error if this rejects. */
  onCreate: (payload: CreateTicketPayload) => Promise<void>;
}

export function CreateTicketForm({ onCreate }: CreateTicketFormProps) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [priority, setPriority] = useState<TicketPriority>(DEFAULT_PRIORITY);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [created, setCreated] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    setCreated(false);

    try {
      await onCreate({
        title: title.trim(),
        description: description.trim() || undefined,
        priority,
      });
      setTitle('');
      setDescription('');
      setPriority(DEFAULT_PRIORITY);
      setCreated(true);
    } catch (err) {
      setError(toErrorMessage(err, 'Failed to create ticket'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="panel form" onSubmit={handleSubmit}>
      <div className="panel-header">
        <h2>New ticket</h2>
        <p>New tickets start as open.</p>
      </div>

      <label>
        Title
        <input
          required
          maxLength={TITLE_MAX_LENGTH}
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          placeholder="e.g. Password reset returns 404"
        />
      </label>

      <label>
        <span>
          Description <span className="optional">(optional)</span>
        </span>
        <textarea
          maxLength={DESCRIPTION_MAX_LENGTH}
          rows={4}
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          placeholder="Steps to reproduce, environment, impact…"
        />
      </label>

      <SelectField
        label="Priority"
        value={priority}
        options={PRIORITY_OPTIONS}
        onChange={setPriority}
      />

      {error && (
        <p className="feedback error" role="alert">
          {error}
        </p>
      )}
      {created && (
        <p className="feedback success" role="status">
          Ticket created
        </p>
      )}

      <button type="submit" disabled={submitting || !title.trim()}>
        {submitting ? 'Creating…' : 'Create ticket'}
      </button>
    </form>
  );
}
