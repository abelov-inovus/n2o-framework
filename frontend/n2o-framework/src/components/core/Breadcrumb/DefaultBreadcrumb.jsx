import React from 'react';
import { Link } from 'react-router-dom';
import isUndefined from 'lodash/isUndefined';
import filter from 'lodash/filter';
import cn from 'classnames';
import Breadcrumb from 'reactstrap/lib/Breadcrumb';
import BreadcrumbItem from 'reactstrap/lib/BreadcrumbItem';

function DefaultBreadcrumb({ items }, props) {
  const itemsWithLabels = filter(items, item => !isUndefined(item.label));

  const crumbs = itemsWithLabels.map(({ label, path }, index) => {
    const lastCrumb = index === itemsWithLabels.length - 1;
    return (
      <BreadcrumbItem key={index} active={lastCrumb}>
        {path && !lastCrumb ? (
          <Link className="n2o-breadcrumb-link" to={path} key={index}>
            {label}
          </Link>
        ) : (
          <span
            className={cn({
              'n2o-breadcrumb-link n2o-breadcrumb-link_active': lastCrumb,
              'n2o-breadcrumb-link n2o-breadcrumb-link_disabled': !lastCrumb,
            })}
            key={index}
          >
            {label}
          </span>
        )}
      </BreadcrumbItem>
    );
  });
  return (
    <>
      <Breadcrumb>{crumbs}</Breadcrumb>
    </>
  );
}

export default DefaultBreadcrumb;
