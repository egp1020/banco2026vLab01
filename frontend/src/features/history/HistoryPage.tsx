import { FormEvent, useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';
import { fetchTransactionsByAccount } from '../../api/transactions';
import { ErrorNotice } from '../../components/ErrorNotice';
import { PaginationBar } from '../../components/PaginationBar';
import { formatCurrency, formatDate } from '../../lib/format';

const PAGE_SIZE = 20;

export function HistoryPage() {
  const [searchParams] = useSearchParams();
  const presetAccount = searchParams.get('account') ?? '';

  const [inputAccount, setInputAccount] = useState(presetAccount);
  const [selectedAccount, setSelectedAccount] = useState(presetAccount);
  const [page, setPage] = useState(0);

  const query = useQuery({
    queryKey: ['transactions', selectedAccount, page],
    queryFn: () => fetchTransactionsByAccount(selectedAccount, page, PAGE_SIZE),
    enabled: selectedAccount.trim().length > 0,
    staleTime: 10_000,
  });

  const transactions = useMemo(() => query.data?.data ?? [], [query.data]);

  const submit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setPage(0);
    setSelectedAccount(inputAccount.trim());
  };

  return (
    <section className="panel">
      <header className="panel__header">
        <h2>Histórico por Cliente</h2>
        <p>Consulta de movimientos por número de cuenta con paginación limpia.</p>
      </header>

      <form className="inline-form" onSubmit={submit}>
        <input
          value={inputAccount}
          onChange={(event) => setInputAccount(event.target.value)}
          placeholder="Número de cuenta"
          inputMode="numeric"
        />
        <button className="btn" type="submit">Consultar</button>
      </form>

      {query.isLoading && <p className="skeleton">Buscando transacciones...</p>}
      {query.error && <ErrorNotice error={query.error} />}

      {!query.isLoading && !query.error && selectedAccount && (
        <>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Fecha</th>
                  <th>Tipo</th>
                  <th>Remitente</th>
                  <th>Receptor</th>
                  <th>Monto</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((tx) => {
                  const isDebit = tx.senderAccountNumber === selectedAccount;
                  return (
                    <tr key={tx.id}>
                      <td>{tx.id}</td>
                      <td>{formatDate(tx.timestamp)}</td>
                      <td>
                        <span className={isDebit ? 'pill pill--debit' : 'pill pill--credit'}>
                          {isDebit ? 'Débito' : 'Crédito'}
                        </span>
                      </td>
                      <td>{tx.senderAccountNumber}</td>
                      <td>{tx.receiverAccountNumber}</td>
                      <td>{formatCurrency(tx.amount)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {query.data && <PaginationBar meta={query.data.meta} onPageChange={setPage} />}
        </>
      )}
    </section>
  );
}
